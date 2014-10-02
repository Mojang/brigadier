package com.mojang.brigadier.arguments;

import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;

public interface CommandArgumentType<T> {
    ParsedArgument<T> parse(String command) throws CommandException;
}
