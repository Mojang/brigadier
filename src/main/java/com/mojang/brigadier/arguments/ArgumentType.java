package com.mojang.brigadier.arguments;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Set;

public interface ArgumentType<T> {
    <S> ParsedArgument<S, T> parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException;

    <S> void listSuggestions(String command, Set<String> output, CommandContextBuilder<S> contextBuilder);
}
