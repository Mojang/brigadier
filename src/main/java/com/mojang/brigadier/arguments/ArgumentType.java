package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Set;

public interface ArgumentType<T> {
    default <S> ParsedArgument<S, T> parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        StringReader reader = new StringReader(command);
        T result = parse(reader, contextBuilder);
        return new ParsedArgument<>(reader.getRead(), result);
    }

    <S> T parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandException;

    default <S> void listSuggestions(String command, Set<String> output, CommandContextBuilder<S> contextBuilder) {}

    default String getUsageSuffix() {
        return null;
    }

    default String getUsageText() {
        return null;
    }
}
