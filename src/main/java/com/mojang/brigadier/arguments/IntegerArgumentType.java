package com.mojang.brigadier.arguments;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

import java.util.Objects;

public class IntegerArgumentType implements ArgumentType<Integer> {
    public static final ParameterizedCommandExceptionType ERROR_NOT_A_NUMBER = new ParameterizedCommandExceptionType("argument.integer.invalid", "Expected an integer, found '${found}'", "found");
    public static final ParameterizedCommandExceptionType ERROR_WRONG_SUFFIX = new ParameterizedCommandExceptionType("argument.integer.wrongsuffix", "Expected suffix '${suffix}'", "suffix");
    public static final ParameterizedCommandExceptionType ERROR_TOO_SMALL = new ParameterizedCommandExceptionType("argument.integer.low", "Integer must not be less than ${minimum}, found ${found}", "found", "minimum");
    public static final ParameterizedCommandExceptionType ERROR_TOO_BIG = new ParameterizedCommandExceptionType("argument.integer.big", "Integer must not be more than ${maximum}, found ${found}", "found", "maximum");

    private final int minimum;
    private final int maximum;
    private final String suffix;

    private IntegerArgumentType(int minimum, int maximum, String suffix) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.suffix = suffix;
    }

    public static IntegerArgumentType integer() {
        return integer(Integer.MIN_VALUE);
    }

    public static IntegerArgumentType integer(int min) {
        return integer(min, Integer.MAX_VALUE);
    }

    public static IntegerArgumentType integer(int min, int max) {
        return integer(min, max, "");
    }

    public static IntegerArgumentType integer(int min, int max, String suffix) {
        return new IntegerArgumentType(min, max, suffix);
    }

    public static int getInteger(CommandContext<?> context, String name) {
        return context.getArgument(name, int.class);
    }

    @Override
    public <S> ParsedArgument<S, Integer> parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        int end = command.indexOf(CommandDispatcher.ARGUMENT_SEPARATOR);
        String raw = command;
        if (end > -1) {
            raw = command.substring(0, end);
        }

        if (raw.length() < suffix.length()) {
            throw ERROR_WRONG_SUFFIX.create(this.suffix);
        }

        String number = raw.substring(0, raw.length() - suffix.length());
        String suffix = raw.substring(number.length());

        if (!suffix.equals(this.suffix)) {
            throw ERROR_WRONG_SUFFIX.create(this.suffix);
        }

        try {
            int value = Integer.parseInt(number);

            if (value < minimum) {
                throw ERROR_TOO_SMALL.create(value, minimum);
            }
            if (value > maximum) {
                throw ERROR_TOO_BIG.create(value, maximum);
            }

            return new ParsedArgument<>(raw, value);
        } catch (NumberFormatException ignored) {
            throw ERROR_NOT_A_NUMBER.create(number);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerArgumentType)) return false;

        IntegerArgumentType that = (IntegerArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum && Objects.equals(suffix, that.suffix);
    }

    @Override
    public int hashCode() {
        return 31 * minimum + maximum;
    }

    @Override
    public String toString() {
        if (minimum == Integer.MIN_VALUE && maximum == Integer.MAX_VALUE) {
            return "integer()";
        } else if (maximum == Integer.MAX_VALUE) {
            return "integer(" + minimum + ")";
        } else {
            return "integer(" + minimum + ", " + maximum + ")";
        }
    }

    @Override
    public String getUsageSuffix() {
        return suffix.length() == 0 ? null : suffix;
    }

    @Override
    public String getUsageText() {
        return "int";
    }
}
