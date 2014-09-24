package net.minecraft.commands.arguments;

import net.minecraft.commands.context.ParsedArgument;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public interface CommandArgumentType<T> {
    ParsedArgument<T> parse(String command) throws IllegalArgumentSyntaxException, ArgumentValidationException;
}
