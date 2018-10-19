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

public interface ArgumentBuilderInterface<S, T extends ArgumentBuilderInterface<S, T>> {
    T then(final ArgumentBuilderInterface<S, ?> argument);

    T then(final CommandNodeInterface<S> argument);

    Collection<CommandNodeInterface<S>> getArguments();

    DefaultCommandNodeDecorator<S, ?> getDefaultNode();

    T executes(final Command<S> command);

    Command<S> getCommand();

    T requires(final Predicate<S> requirement);

    Predicate<S> getRequirement();

    T redirect(final CommandNodeInterface<S> target);

    T redirect(final CommandNodeInterface<S> target, final SingleRedirectModifier<S> modifier);

    T fork(final CommandNodeInterface<S> target, final RedirectModifier<S> modifier);

    T forward(final CommandNodeInterface<S> target, final RedirectModifier<S> modifier, final boolean fork);

    CommandNodeInterface<S> getRedirect();

    RedirectModifier<S> getRedirectModifier();

    boolean isFork();

    CommandNodeInterface<S> build();
}
