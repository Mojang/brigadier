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

public abstract class ArgumentBuilder<S, R, T extends ArgumentBuilder<S, R, T>> {
    private final RootCommandNode<S, R> arguments = new RootCommandNode<>();
    private Command<S, R> command;
    private Predicate<S> requirement = s -> true;
    private CommandNode<S, R> target;
    private RedirectModifier<S, R> modifier = null;
    private boolean forks;

    protected abstract T getThis();

    public T then(final ArgumentBuilder<S, R, ?> argument) {
        if (target != null) {
            throw new IllegalStateException("Cannot add children to a redirected node");
        }
        arguments.addChild(argument.build());
        return getThis();
    }

    public T then(final CommandNode<S, R> argument) {
        if (target != null) {
            throw new IllegalStateException("Cannot add children to a redirected node");
        }
        arguments.addChild(argument);
        return getThis();
    }

    public Collection<CommandNode<S, R>> getArguments() {
        return arguments.getChildren();
    }

    public T executes(final Command<S, R> command) {
        this.command = command;
        return getThis();
    }

    public Command<S, R> getCommand() {
        return command;
    }

    public T requires(final Predicate<S> requirement) {
        this.requirement = requirement;
        return getThis();
    }

    public Predicate<S> getRequirement() {
        return requirement;
    }

    public T redirect(final CommandNode<S, R> target) {
        return forward(target, null, false);
    }

    public T redirect(final CommandNode<S, R> target, final SingleRedirectModifier<S, R> modifier) {
        return forward(target, modifier == null ? null : o -> Collections.singleton(modifier.apply(o)), false);
    }

    public T fork(final CommandNode<S, R> target, final RedirectModifier<S, R> modifier) {
        return forward(target, modifier, true);
    }

    public T forward(final CommandNode<S, R> target, final RedirectModifier<S, R> modifier, final boolean fork) {
        if (!arguments.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot forward a node with children");
        }
        this.target = target;
        this.modifier = modifier;
        this.forks = fork;
        return getThis();
    }

    public CommandNode<S, R> getRedirect() {
        return target;
    }

    public RedirectModifier<S, R> getRedirectModifier() {
        return modifier;
    }

    public boolean isFork() {
        return forks;
    }

    public abstract CommandNode<S, R> build();
}
