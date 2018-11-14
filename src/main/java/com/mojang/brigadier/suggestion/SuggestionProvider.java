// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.concurrent.CompletableFuture;

/**
 * A provider that generates suggestions based on a context and adds them to a builder it then returns.
 * <p>
 * As looking up the suggestions might involve about anything, from querying game state to I/O, the method returns a
 * {@link CompletableFuture} and no guarantees are made about its execution.
 *
 * @param <S> the type of the command source
 */
@FunctionalInterface
public interface SuggestionProvider<S> {
    /**
     * Computes suggestions for the given {@link CommandContext} and adds them to the given {@link SuggestionsBuilder},
     * which is then build and returned in the future.
     *
     * @param context the command context to as a base
     * @param builder the builder to add them to
     * @return a completable future that might complete sometime in the future, as computing the suggestions might
     * involve arbitrary computations and even I/O
     * @throws CommandSyntaxException if an error occurs parsing the context to return a valid suggestion
     */
    CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) throws CommandSyntaxException;
}
