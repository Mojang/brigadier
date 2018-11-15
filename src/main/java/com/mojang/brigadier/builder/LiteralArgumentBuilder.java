// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

/**
 * An {@link ArgumentBuilder} for a {@link LiteralCommandNode} that is triggered by a literal (i.e. fixed) keyword.
 * <p>
 * An example would be a command like "ping", that is identified by its keyword.
 *
 * @param <S> the type of the command source
 */
public class LiteralArgumentBuilder<S> extends ArgumentBuilder<S, LiteralArgumentBuilder<S>> {
    private final String literal;

    /**
     * Creates a new LiteralArgumentBuilder with the given literal
     *
     * @param literal the literal that identifies the built command node
     */
    protected LiteralArgumentBuilder(final String literal) {
        this.literal = literal;
    }

    /**
     * A factory method to create a new builder for a literal command node.
     *
     * @param name the literal the built command node should be identified by
     * @param <S> the type of the command source
     * @return the created LiteralArgumentBuilder
     */
    public static <S> LiteralArgumentBuilder<S> literal(final String name) {
        return new LiteralArgumentBuilder<>(name);
    }

    @Override
    protected LiteralArgumentBuilder<S> getThis() {
        return this;
    }

    /**
     * Returns the literal that the built command node will be identified by.
     *
     * @return the literal that the built command node will be identified by
     */
    public String getLiteral() {
        return literal;
    }

    @Override
    public LiteralCommandNode<S> build() {
        final LiteralCommandNode<S> result = new LiteralCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
