package com.mojang.brigadier.builder;

import com.mojang.brigadier.CommandSuggestions;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RequiredArgumentBuilder<S, T> extends ArgumentBuilder<S, RequiredArgumentBuilder<S, T>> {
    private final String name;
    private final ArgumentType<T> type;
    private CommandSuggestions.Provider<S> suggestionsProvider = null;

    private RequiredArgumentBuilder(final String name, final ArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <S, T> RequiredArgumentBuilder<S, T> argument(final String name, final ArgumentType<T> type) {
        return new RequiredArgumentBuilder<>(name, type);
    }

    public RequiredArgumentBuilder<S, T> suggests(final CommandSuggestions.Provider<S> provider) {
        this.suggestionsProvider = provider;
        return getThis();
    }

    public CommandSuggestions.Provider<S> getSuggestionsProvider() {
        return suggestionsProvider == null ? type::listSuggestions : suggestionsProvider;
    }

    @Override
    protected RequiredArgumentBuilder<S, T> getThis() {
        return this;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArgumentCommandNode<S, T> build() {
        final ArgumentCommandNode<S, T> result = new ArgumentCommandNode<>(getName(), getType(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), getSuggestionsProvider());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
