package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

public class FloatArgumentType implements ArgumentType<Float> {
    public static final ParameterizedCommandExceptionType ERROR_TOO_SMALL = new ParameterizedCommandExceptionType("argument.float.low", "Float must not be less than ${minimum}, found ${found}", "found", "minimum");
    public static final ParameterizedCommandExceptionType ERROR_TOO_BIG = new ParameterizedCommandExceptionType("argument.float.big", "Float must not be more than ${maximum}, found ${found}", "found", "maximum");

    private final float minimum;
    private final float maximum;

    private FloatArgumentType(final float minimum, final float maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static FloatArgumentType floatArg() {
        return floatArg(-Float.MAX_VALUE);
    }

    public static FloatArgumentType floatArg(final float min) {
        return floatArg(min, Float.MAX_VALUE);
    }

    public static FloatArgumentType floatArg(final float min, final float max) {
        return new FloatArgumentType(min, max);
    }

    public static float getFloat(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Float.class);
    }

    public float getMinimum() {
        return minimum;
    }

    public float getMaximum() {
        return maximum;
    }

    @Override
    public <S> Float parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final float result = (float) reader.readDouble();
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
        if (!(o instanceof FloatArgumentType)) return false;

        final FloatArgumentType that = (FloatArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return (int) (31 * minimum + maximum);
    }

    @Override
    public String toString() {
        if (minimum == -Float.MAX_VALUE && maximum == Float.MAX_VALUE) {
            return "float()";
        } else if (maximum == Float.MAX_VALUE) {
            return "float(" + minimum + ")";
        } else {
            return "float(" + minimum + ", " + maximum + ")";
        }
    }
}
