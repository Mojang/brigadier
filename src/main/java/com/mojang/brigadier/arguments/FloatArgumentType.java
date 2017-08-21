package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

import java.util.Objects;

public class FloatArgumentType implements ArgumentType<Float> {
    public static final ParameterizedCommandExceptionType ERROR_WRONG_SUFFIX = new ParameterizedCommandExceptionType("argument.float.wrongsuffix", "Expected suffix '${suffix}'", "suffix");
    public static final ParameterizedCommandExceptionType ERROR_TOO_SMALL = new ParameterizedCommandExceptionType("argument.float.low", "Float must not be less than ${minimum}, found ${found}", "found", "minimum");
    public static final ParameterizedCommandExceptionType ERROR_TOO_BIG = new ParameterizedCommandExceptionType("argument.float.big", "Float must not be more than ${maximum}, found ${found}", "found", "maximum");

    private final float minimum;
    private final float maximum;
    private final String suffix;

    private FloatArgumentType(final float minimum, final float maximum, final String suffix) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.suffix = suffix;
    }

    public static FloatArgumentType floatArg() {
        return floatArg(-Float.MAX_VALUE);
    }

    public static FloatArgumentType floatArg(final float min) {
        return floatArg(min, Float.MAX_VALUE);
    }

    public static FloatArgumentType floatArg(final float min, final float max) {
        return floatArg(min, max, "");
    }

    public static FloatArgumentType floatArg(final float min, final float max, final String suffix) {
        return new FloatArgumentType(min, max, suffix);
    }

    public static int getInteger(final CommandContext<?> context, final String name) {
        return context.getArgument(name, int.class);
    }

    @Override
    public <S> Float parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final float result = (float) reader.readDouble();
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
        if (!(o instanceof FloatArgumentType)) return false;

        final FloatArgumentType that = (FloatArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum && Objects.equals(suffix, that.suffix);
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

    @Override
    public String getUsageSuffix() {
        return suffix.length() == 0 ? null : suffix;
    }

    @Override
    public String getUsageText() {
        return "float";
    }
}
