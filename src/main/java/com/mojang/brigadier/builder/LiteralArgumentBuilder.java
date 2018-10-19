// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNodeInterface;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LiteralArgumentBuilder<S> extends ArgumentBuilder<S, LiteralArgumentBuilder<S>> {
    private final String literal;

    protected LiteralArgumentBuilder(final String literal) {
        this.literal = literal;
    }

    public static <S> LiteralArgumentBuilder<S> literal(final String name) {
        return new LiteralArgumentBuilder<>(name);
    }

    public static <S> DefaultArgumentBuilderDecorator<S, Void> defaultLiteral(final String name) {
        return new DefaultArgumentBuilderDecorator<>(literal(name), null);
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
        final LiteralCommandNode<S> result = new LiteralCommandNode<>(getLiteral(), getCommand(), getDefaultNode(),getRequirement(), getRedirect(), getRedirectModifier(), isFork());

        for (final CommandNodeInterface<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
