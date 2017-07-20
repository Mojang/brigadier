package com.mojang.brigadier.arguments;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.FixedParsedArgument;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class BoolArgumentType implements ArgumentType<Boolean> {
    public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType("argument.bool.invalid", "Value must be true or false");

    private BoolArgumentType() {
    }

    public static BoolArgumentType bool() {
        return new BoolArgumentType();
    }

    public static boolean getBool(CommandContext<?> context, String name) {
        return context.getArgument(name, Boolean.class);
    }

    @Override
    public <S> ParsedArgument<S, Boolean> parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        int end = command.indexOf(CommandDispatcher.ARGUMENT_SEPARATOR);
        String raw = command;
        if (end > -1) {
            raw = command.substring(0, end);
        }

        if (raw.equals("true")) {
            return new FixedParsedArgument<>(raw, true);
        } else if (raw.equals("false")) {
            return new FixedParsedArgument<>(raw, false);
        } else {
            throw ERROR_INVALID.create();
        }
    }

    @Override
    public String getUsageText() {
        return "bool";
    }
}
