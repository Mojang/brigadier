// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * The root of the command node tree, which has basically no functionality except to hold the tree together.
 *
 * @param <S> the type of the command source
 */
public class RootCommandNode<S> extends CommandNode<S> {
    public RootCommandNode() {
        super(null, c -> true, null, s -> Collections.singleton(s.getSource()), false);
    }

    /**
     * Returns an empty string
     *
     * @return an empty string
     */
    @Override
    public String getName() {
        return "";
    }

    /**
     * Returns an empty string
     *
     * @return an empty string
     */
    @Override
    public String getUsageText() {
        return "";
    }

    /**
     * Is a NOP.
     *
     * @param reader {@inheritDoc}
     * @param contextBuilder {@inheritDoc}
     */
    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
    }

    /**
     * Returns an empty {@link Suggestions} object.
     *
     * @param context {@inheritDoc}
     * @param builder {@inheritDoc}
     * @return an empty {@link Suggestions} object
     */
    @Override
    public CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    /**
     * Always returns false.
     *
     * @param input {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isValidInput(final String input) {
        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCommandNode)) return false;
        return super.equals(o);
    }

    /**
     * Always throws an {@link IllegalStateException}.
     *
     * @return nothing
     * @throws IllegalStateException if you invoke it
     */
    @Override
    public ArgumentBuilder<S, ?> createBuilder() {
        throw new IllegalStateException("Cannot convert root into a builder");
    }

    /**
     * Returns an empty string
     *
     * @return an empty string
     */
    @Override
    protected String getSortedKey() {
        return "";
    }

    /**
     * Returns an empty immutable collection
     *
     * @return an empty immutable collection
     */
    @Override
    public Collection<String> getExamples() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "<root>";
    }
}
