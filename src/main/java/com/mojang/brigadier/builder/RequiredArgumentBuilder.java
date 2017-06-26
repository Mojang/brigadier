package com.mojang.brigadier.builder;

import com.mojang.brigadier.arguments.CommandArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

public class RequiredArgumentBuilder<S, T> extends ArgumentBuilder<S, RequiredArgumentBuilder<S, T>> {
    private final String name;
    private final CommandArgumentType<T> type;

    private RequiredArgumentBuilder(String name, CommandArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <S, T> RequiredArgumentBuilder<S, T> argument(String name, CommandArgumentType<T> type) {
        return new RequiredArgumentBuilder<>(name, type);
    }

    @Override
    protected RequiredArgumentBuilder<S, T> getThis() {
        return this;
    }

    private CommandArgumentType<T> getType() {
        return type;
    }

    private String getName() {
        return name;
    }

    public ArgumentCommandNode<S, T> build() {
        ArgumentCommandNode<S, T> result = new ArgumentCommandNode<>(getName(), getType(), getCommand());

        for (CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
