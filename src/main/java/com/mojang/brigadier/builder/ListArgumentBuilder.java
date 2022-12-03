package com.mojang.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.ListCommandNode;

public class ListArgumentBuilder<S, T> extends RequiredArgumentBuilder<S, T> {
    protected ListArgumentBuilder(String name, ArgumentType<T> type) {
        super(name, type);
    }

    public static <S, T> ListArgumentBuilder<S, T> list(final String name, final ArgumentType<T> type) {
        return new ListArgumentBuilder<>(name, type);
    }

    public ArgumentCommandNode<S, T> build() {
        final ListCommandNode<S, T> result = new ListCommandNode<>(getName(), getType(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getSuggestionsProvider());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
