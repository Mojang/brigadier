package com.mojang.brigadier.arguments;

import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Set;

public interface CommandArgumentType<T> {
    <S> ParsedArgument<S, T> parse(String command) throws CommandException;

    void listSuggestions(String command, Set<String> output);
}
