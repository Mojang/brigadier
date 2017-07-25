package com.mojang.brigadier.arguments;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
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
    public <S> Boolean parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandException {
        return reader.readBoolean();
    }

    @Override
    public String getUsageText() {
        return "bool";
    }
}
