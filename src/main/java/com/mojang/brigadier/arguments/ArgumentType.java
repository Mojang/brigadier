// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * A parsable argument designed to be used by an {@link ArgumentCommandNode} as its type argument.
 * <p>
 * As different commands need different arguments (string, integer, long, double,…), this interface provides a
 * way to define parsers for each of them.
 *
 * @param <T> the type of the argument
 */
public interface ArgumentType<T> {

    /**
     * Parses the given {@link StringReader} to an instance of this argument's generic type.
     *
     * <p>For example, if this this is an {@code ArgumentType<Integer>} this would return an Integer.</p>
     *
     * @param reader the read to read from
     * @return the parsed argument
     * @throws CommandSyntaxException if the argument is malformed
     */
    T parse(StringReader reader) throws CommandSyntaxException;

    /**
     * Gets suggestions for a parsed input string on what comes next.
     *
     * <p>As it is ultimately up to custom argument types to provide suggestions, it may be an asynchronous operation,
     * for example getting in-game data or player names etc. As such, this method returns a future and no guarantees
     * are made to when or how the future completes.</p>
     *
     * <p>The suggestions provided will be in the context of the end of the parsed input string, but may suggest
     * new or replacement strings for earlier in the input string. For example, if the end of the string was
     * {@code foobar} but an argument preferred it to be {@code minecraft:foobar}, it will suggest a replacement for that
     * whole segment of the input.</p>
     *
     * @param context the context to get them for
     * @param builder the suggestions builder to add them to
     * @return a future that will eventually resolve into a {@link Suggestions} object
     * @implSpec The default implementation simply returns an empty suggestions object
     */
    default <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    /**
     * Provides examples of valid arguments that are used by
     * {@link CommandDispatcher#findAmbiguities} to find ambiguities.
     *
     * <p>No guarantees about the mutability of the returned collection are made.</p>
     *
     * @return a collection with a few examples for valid type arguments
     * @implSpec The default implementation returns an emtpy immutable collection.
     */
    default Collection<String> getExamples() {
        return Collections.emptyList();
    }
}
