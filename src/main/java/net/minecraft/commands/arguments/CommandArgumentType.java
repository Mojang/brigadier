package net.minecraft.commands.arguments;

import net.minecraft.commands.context.ParsedArgument;
import net.minecraft.commands.exceptions.CommandException;

public interface CommandArgumentType<T> {
    ParsedArgument<T> parse(String command) throws CommandException;
}
