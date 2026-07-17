// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LiteralArgumentBuilder<S> extends ArgumentBuilder<S, LiteralArgumentBuilder<S>> {
    private final String literal;

    private final boolean ignoreCase;

    public LiteralArgumentBuilder(final String literal) {
        this(literal, false);
    }

    public LiteralArgumentBuilder(final String literal, final boolean ignoreCase) {
        this.literal = literal;
        this.ignoreCase = ignoreCase;
    }

    public static <S> LiteralArgumentBuilder<S> literal(final String name) {
        return new LiteralArgumentBuilder<>(name, false);
    }
    
    public static <S> LiteralArgumentBuilder<S> literal(final String name, final boolean ignoreCase) {
        return new LiteralArgumentBuilder<>(name, ignoreCase);
    }

    @Override
    protected LiteralArgumentBuilder<S> getThis() {
        return this;
    }

    public String getLiteral() {
        return literal;
    }

    public boolean ignoreCase() { return ignoreCase; }

    @Override
    public LiteralCommandNode<S> build() {
        final LiteralCommandNode<S> result = new LiteralCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), ignoreCase());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
