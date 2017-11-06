package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

import java.util.Objects;

public class IntegerArgumentType implements ArgumentType<Integer> {
    public static final ParameterizedCommandExceptionType ERROR_WRONG_SUFFIX = new ParameterizedCommandExceptionType("argument.integer.wrongsuffix", "Expected suffix '${suffix}'", "suffix");
    public static final ParameterizedCommandExceptionType ERROR_TOO_SMALL = new ParameterizedCommandExceptionType("argument.integer.low", "Integer must not be less than ${minimum}, found ${found}", "found", "minimum");
    public static final ParameterizedCommandExceptionType ERROR_TOO_BIG = new ParameterizedCommandExceptionType("argument.integer.big", "Integer must not be more than ${maximum}, found ${found}", "found", "maximum");

    private final int minimum;
    private final int maximum;
    private final String suffix;

    private IntegerArgumentType(final int minimum, final int maximum, final String suffix) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.suffix = suffix;
    }

    public static IntegerArgumentType integer() {
        return integer(Integer.MIN_VALUE);
    }

    public static IntegerArgumentType integer(final int min) {
        return integer(min, Integer.MAX_VALUE);
    }

    public static IntegerArgumentType integer(final int min, final int max) {
        return integer(min, max, "");
    }

    public static IntegerArgumentType integer(final int min, final int max, final String suffix) {
        return new IntegerArgumentType(min, max, suffix);
    }

    public static int getInteger(final CommandContext<?> context, final String name) {
        return context.getArgument(name, int.class);
    }

    @Override
    public <S> Integer parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int result = reader.readInt();
        for (int i = 0; i < suffix.length(); i++) {
            if (reader.canRead() && reader.peek() == suffix.charAt(i)) {
                reader.skip();
            } else {
                reader.setCursor(start);
                throw ERROR_WRONG_SUFFIX.createWithContext(reader, suffix);
            }
        }
        if (result < minimum) {
            reader.setCursor(start);
            throw ERROR_TOO_SMALL.createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw ERROR_TOO_BIG.createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerArgumentType)) return false;

        final IntegerArgumentType that = (IntegerArgumentType) o;
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
}
