package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LiteralArgumentBuilder<S> extends ArgumentBuilder<S, LiteralArgumentBuilder<S>> {
    private final String literal;

    protected LiteralArgumentBuilder(String literal) {
        this.literal = literal;
    }

    public static <S> LiteralArgumentBuilder<S> literal(String name) {
        return new LiteralArgumentBuilder<>(name);
    }

    @Override
    protected LiteralArgumentBuilder<S> getThis() {
        return this;
    }

    private String getLiteral() {
        return literal;
    }

    @Override
    public LiteralCommandNode<S> build() {
        LiteralCommandNode<S> result = new LiteralCommandNode<>(getLiteral(), getCommand(), getRequirement());

        for (CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
