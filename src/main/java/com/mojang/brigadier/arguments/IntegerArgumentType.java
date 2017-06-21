package com.mojang.brigadier.arguments;

import com.google.common.base.Splitter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

public class IntegerArgumentType implements CommandArgumentType<Integer> {
    public static final ParameterizedCommandExceptionType ERROR_NOT_A_NUMBER = new ParameterizedCommandExceptionType("argument-integer-invalid", "Expected an integer, found '${found}'", "found");
    public static final ParameterizedCommandExceptionType ERROR_TOO_SMALL = new ParameterizedCommandExceptionType("argument-integer-low", "Integer must not be less than ${minimum}, found ${found}", "found", "minimum");
    public static final ParameterizedCommandExceptionType ERROR_TOO_BIG = new ParameterizedCommandExceptionType("argument-integer-big", "Integer must not be more than ${maximum}, found ${found}", "found", "maximum");

    private static final Splitter SPLITTER = Splitter.on(CommandDispatcher.ARGUMENT_SEPARATOR).limit(2);

    private final int minimum;
    private final int maximum;

    private IntegerArgumentType(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static IntegerArgumentType integer() {
        return integer(Integer.MIN_VALUE);
    }

    public static IntegerArgumentType integer(int min) {
        return integer(min, Integer.MAX_VALUE);
    }

    public static IntegerArgumentType integer(int min, int max) {
        return new IntegerArgumentType(min, max);
    }

    public static int getInteger(CommandContext<?> context, String name) {
        return context.getArgument(name, int.class).getResult();
    }

    @Override
    public ParsedArgument<Integer> parse(String command) throws CommandException {
        String raw = SPLITTER.split(command).iterator().next();

        try {
            int value = Integer.parseInt(raw);

            if (value < minimum) {
                throw ERROR_TOO_SMALL.create(value, minimum);
            }
            if (value > maximum) {
                throw ERROR_TOO_BIG.create(value, maximum);
            }

            return new ParsedArgument<>(raw, value);
        } catch (NumberFormatException ignored) {
            throw ERROR_NOT_A_NUMBER.create(raw);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerArgumentType)) return false;

        IntegerArgumentType that = (IntegerArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
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
}
