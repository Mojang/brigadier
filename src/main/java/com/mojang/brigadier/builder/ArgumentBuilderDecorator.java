// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.tree.CommandNodeInterface;
import com.mojang.brigadier.tree.DefaultCommandNodeDecorator;

import java.util.Collection;
import java.util.function.Predicate;

abstract class ArgumentBuilderDecorator<S, T extends ArgumentBuilderDecorator<S, T>> implements ArgumentBuilderInterface<S, T> {
    private final ArgumentBuilderInterface<S, ?> delegate;

    protected ArgumentBuilderDecorator(final ArgumentBuilderInterface<S, ?> delegate) {
        if(delegate == null) throw new NullPointerException("Delegated object cannot be a null pointer");
        this.delegate = delegate;
    }

    protected abstract T getThis();

    public ArgumentBuilderInterface<S, ?> getDelegate() {
        return delegate;
    }

    @Override
    public T then(final ArgumentBuilderInterface<S, ?> argument) {
        delegate.then(argument);
        return getThis();
    }

    @Override
    public T then(final CommandNodeInterface<S> argument) {
        delegate.then(argument);
        return getThis();
    }

    @Override
    public Collection<CommandNodeInterface<S>> getArguments() {
        return delegate.getArguments();
    }

    @Override
    public DefaultCommandNodeDecorator<S, ?> getDefaultNode() {
        return delegate.getDefaultNode();
    }

    @Override
    public T executes(final Command<S> command) {
        delegate.executes(command);
        return getThis();
    }

    @Override
    public Command<S> getCommand() {
        return delegate.getCommand();
    }

    @Override
    public T requires(final Predicate<S> requirement) {
        delegate.requires(requirement);
        return getThis();
    }

    @Override
    public Predicate<S> getRequirement() {
        return delegate.getRequirement();
    }

    @Override
    public T redirect(final CommandNodeInterface<S> target) {
        delegate.redirect(target);
        return getThis();
    }

    @Override
    public T redirect(final CommandNodeInterface<S> target, final SingleRedirectModifier<S> modifier) {
        delegate.redirect(target, modifier);
        return getThis();
    }

    @Override
    public T fork(final CommandNodeInterface<S> target, final RedirectModifier<S> modifier) {
        delegate.fork(target, modifier);
        return getThis();
    }

    @Override
    public T forward(final CommandNodeInterface<S> target, final RedirectModifier<S> modifier, final boolean fork) {
        delegate.forward(target, modifier, fork);
        return getThis();
    }

    @Override
    public CommandNodeInterface<S> getRedirect() {
        return delegate.getRedirect();
    }

    @Override
    public RedirectModifier<S> getRedirectModifier() {
        return delegate.getRedirectModifier();
    }

    @Override
    public boolean isFork() {
        return delegate.isFork();
    }

    @Override
    public CommandNodeInterface<S> build() {
        return delegate.build();
    }
}
