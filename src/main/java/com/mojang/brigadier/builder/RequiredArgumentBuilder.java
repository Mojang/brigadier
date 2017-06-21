package com.mojang.brigadier.builder;

import com.mojang.brigadier.arguments.CommandArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

public class RequiredArgumentBuilder<T> extends ArgumentBuilder<RequiredArgumentBuilder<T>> {
    private final String name;
    private final CommandArgumentType<T> type;

    protected RequiredArgumentBuilder(String name, CommandArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <T> RequiredArgumentBuilder<T> argument(String name, CommandArgumentType<T> type) {
        return new RequiredArgumentBuilder<>(name, type);
    }

    @Override
    protected RequiredArgumentBuilder<T> getThis() {
        return this;
    }

    public CommandArgumentType<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArgumentCommandNode<T> build() {
        ArgumentCommandNode<T> result = new ArgumentCommandNode<>(getName(), getType(), getCommand());

        for (CommandNode argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
