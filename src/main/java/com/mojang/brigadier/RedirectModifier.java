// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;

/**
 * A redirect modifier to apply when a command is redirected and/or forked.
 * <p>
 * A redirect modifier takes a command context with a single command source and returns a collection of multiple
 * command sources. The target command is then invoked for each of the returned command sources.
 *
 * @param <S> the type of the command source
 */
@FunctionalInterface
public interface RedirectModifier<S> {

    /**
     * Applies the modifier to the context, so it creates a list of command sources to invoke the target command for.
     *
     * @param context the context to base it on
     * @return a collection with command sources to invoke the command for
     * @throws CommandSyntaxException if an error occurred
     */
    Collection<S> apply(CommandContext<S> context) throws CommandSyntaxException;
}
