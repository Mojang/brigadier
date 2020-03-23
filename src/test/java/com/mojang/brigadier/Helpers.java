// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public class Helpers {
    public static <S> LiteralArgumentBuilder<S, Integer> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <S, T> RequiredArgumentBuilder<S, Integer, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static <S> CommandDispatcher<S, Integer> create() {
        return new CommandDispatcher<>(0, Integer::sum, i -> i);
    }
}