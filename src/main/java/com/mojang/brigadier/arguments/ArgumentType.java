package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Set;

public interface ArgumentType<T> {
    <S> T parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandException;

    default <S> void listSuggestions(final String command, final Set<String> output, final CommandContextBuilder<S> contextBuilder) {
    }

    default String getUsageSuffix() {
        return null;
    }

    default String getUsageText() {
        return null;
    }
}
