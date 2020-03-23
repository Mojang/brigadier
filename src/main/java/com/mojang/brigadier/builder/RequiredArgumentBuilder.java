// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

public class RequiredArgumentBuilder<S, R, T> extends ArgumentBuilder<S, R, RequiredArgumentBuilder<S, R, T>> {
    private final String name;
    private final ArgumentType<T> type;
    private SuggestionProvider<S, R> suggestionsProvider = null;

    private RequiredArgumentBuilder(final String name, final ArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <S, R, T> RequiredArgumentBuilder<S, R, T> argument(final String name, final ArgumentType<T> type) {
        return new RequiredArgumentBuilder<>(name, type);
    }

    public RequiredArgumentBuilder<S, R, T> suggests(final SuggestionProvider<S, R> provider) {
        this.suggestionsProvider = provider;
        return getThis();
    }

    public SuggestionProvider<S, R> getSuggestionsProvider() {
        return suggestionsProvider;
    }

    @Override
    protected RequiredArgumentBuilder<S, R, T> getThis() {
        return this;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArgumentCommandNode<S, R, T> build() {
        final ArgumentCommandNode<S, R, T> result = new ArgumentCommandNode<>(getName(), getType(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getSuggestionsProvider());

        for (final CommandNode<S, R> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
