// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S, T>> {
    private final RootCommandNode<S> arguments = new RootCommandNode<>();
    private CommandNode<S> defaultNode;
    private final boolean isDefaultNode;
    private Command<S> command;
    private Predicate<S> requirement = s -> true;
    private CommandNode<S> target;
    private RedirectModifier<S> modifier = null;
    private boolean forks;

    protected ArgumentBuilder(final boolean isDefaultNode) {
        this.isDefaultNode = isDefaultNode;
    }

    protected abstract T getThis();

    public T then(final ArgumentBuilder<S, ?> argument) {
        return then(argument.build());
    }

    public T then(final CommandNode<S> argument) {
        if (target != null) {
            throw new IllegalStateException("Cannot add children to a redirected node");
        }

        if(argument.isDefaultNode())
            if (defaultNode != null)
                throw new IllegalStateException("Cannot add multiple default nodes as child of one node");
            else
                defaultNode = argument;

        arguments.addChild(argument);
        return getThis();
    }

    public Collection<CommandNode<S>> getArguments() {
        return arguments.getChildren();
    }

    public CommandNode<S> getDefaultNode() {
        return defaultNode;
    }

    public boolean isDefaultNode() {
        return isDefaultNode;
    }

    public T executes(final Command<S> command) {
        this.command = command;
        return getThis();
    }

    public Command<S> getCommand() {
        return command;
    }

    public T requires(final Predicate<S> requirement) {
        this.requirement = requirement;
        return getThis();
    }

    public Predicate<S> getRequirement() {
        return requirement;
    }

    public T redirect(final CommandNode<S> target) {
        return forward(target, null, false);
    }

    public T redirect(final CommandNode<S> target, final SingleRedirectModifier<S> modifier) {
        return forward(target, modifier == null ? null : o -> Collections.singleton(modifier.apply(o)), false);
    }

    public T fork(final CommandNode<S> target, final RedirectModifier<S> modifier) {
        return forward(target, modifier, true);
    }

    public T forward(final CommandNode<S> target, final RedirectModifier<S> modifier, final boolean fork) {
        if (!arguments.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot forward a node with children");
        }
        this.target = target;
        this.modifier = modifier;
        this.forks = fork;
        return getThis();
    }

    public CommandNode<S> getRedirect() {
        return target;
    }

    public RedirectModifier<S> getRedirectModifier() {
        return modifier;
    }

    public boolean isFork() {
        return forks;
    }

    public abstract CommandNode<S> build();
}
