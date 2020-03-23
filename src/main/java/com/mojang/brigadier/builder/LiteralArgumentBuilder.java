// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LiteralArgumentBuilder<S, R> extends ArgumentBuilder<S, R, LiteralArgumentBuilder<S, R>> {
    private final String literal;

    protected LiteralArgumentBuilder(final String literal) {
        this.literal = literal;
    }

    public static <S, R> LiteralArgumentBuilder<S, R> literal(final String name) {
        return new LiteralArgumentBuilder<>(name);
    }

    @Override
    protected LiteralArgumentBuilder<S, R> getThis() {
        return this;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public LiteralCommandNode<S, R> build() {
        final LiteralCommandNode<S, R> result = new LiteralCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork());

        for (final CommandNode<S, R> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
