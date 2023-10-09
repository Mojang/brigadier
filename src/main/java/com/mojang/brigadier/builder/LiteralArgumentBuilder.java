// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LiteralArgumentBuilder<S> extends ArgumentBuilder<S, LiteralArgumentBuilder<S>> {
    private final String literal;

    protected LiteralArgumentBuilder(final String literal, final boolean isDefaultNode) {
        super(isDefaultNode);
        this.literal = literal;
    }

    public static <S> LiteralArgumentBuilder<S> literal(final String name) {
        return new LiteralArgumentBuilder<>(name, false);
    }

    public static <S> LiteralArgumentBuilder<S> defaultLiteral(final String name) {
        return new LiteralArgumentBuilder<>(name, true);
    }

    @Override
    protected LiteralArgumentBuilder<S> getThis() {
        return this;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public LiteralCommandNode<S> build() {
        final LiteralCommandNode<S> result = new LiteralCommandNode<>(getLiteral(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getDefaultNode(), isDefaultNode());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
