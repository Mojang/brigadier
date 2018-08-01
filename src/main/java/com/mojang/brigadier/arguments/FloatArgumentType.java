// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class FloatArgumentType implements ArgumentType<Float> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");

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
    public Float parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final float result = reader.readFloat();
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooHigh().createWithContext(reader, result, maximum);
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

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
