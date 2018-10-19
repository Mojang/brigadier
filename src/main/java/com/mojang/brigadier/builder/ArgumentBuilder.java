// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.tree.CommandNodeInterface;
import com.mojang.brigadier.tree.DefaultCommandNodeDecorator;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S, T>> implements ArgumentBuilderInterface<S, T> {
    private final RootCommandNode<S> arguments = new RootCommandNode<>();
    private DefaultCommandNodeDecorator<S, ?> defaultNode;
    private Command<S> command;
    private Predicate<S> requirement = s -> true;
    private CommandNodeInterface<S> target;
    private RedirectModifier<S> modifier = null;
    private boolean forks;

    protected abstract T getThis();

    @Override
    public T then(final ArgumentBuilderInterface<S, ?> argument) {
        return then(argument.build());
    }

    @Override
    public T then(final CommandNodeInterface<S> argument) {
        if (target != null) {
            throw new IllegalStateException("Cannot add children to a redirected node");
        }

        if(argument instanceof DefaultCommandNodeDecorator)
            if (defaultNode != null)
                throw new IllegalStateException("Cannot add multiple default nodes as child of one node");
            else
                defaultNode = (DefaultCommandNodeDecorator<S, ?>) argument;

        arguments.addChild(argument);

        return getThis();
    }

    @Override
    public Collection<CommandNodeInterface<S>> getArguments() {
        return arguments.getChildren();
    }

    @Override
    public DefaultCommandNodeDecorator<S, ?> getDefaultNode() {
        return defaultNode;
    }

    @Override
    public T executes(final Command<S> command) {
        this.command = command;
        return getThis();
    }

    @Override
    public Command<S> getCommand() {
        return command;
    }

    @Override
    public T requires(final Predicate<S> requirement) {
        this.requirement = requirement;
        return getThis();
    }

    @Override
    public Predicate<S> getRequirement() {
        return requirement;
    }

    @Override
    public T redirect(final CommandNodeInterface<S> target) {
        return forward(target, null, false);
    }

    @Override
    public T redirect(final CommandNodeInterface<S> target, final SingleRedirectModifier<S> modifier) {
        return forward(target, modifier == null ? null : o -> Collections.singleton(modifier.apply(o)), false);
    }

    @Override
    public T fork(final CommandNodeInterface<S> target, final RedirectModifier<S> modifier) {
        return forward(target, modifier, true);
    }

    @Override
    public T forward(final CommandNodeInterface<S> target, final RedirectModifier<S> modifier, final boolean fork) {
        if (!arguments.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot forward a node with children");
        }
        this.target = target;
        this.modifier = modifier;
        this.forks = fork;
        return getThis();
    }

    @Override
    public CommandNodeInterface<S> getRedirect() {
        return target;
    }

    @Override
    public RedirectModifier<S> getRedirectModifier() {
        return modifier;
    }

    @Override
    public boolean isFork() {
        return forks;
    }

}
