// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class LongArgumentType implements ArgumentType<Long> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    private final long minimum;
    private final long maximum;

    private LongArgumentType(final long minimum, final long maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static LongArgumentType longArg() {
        return longArg(Long.MIN_VALUE);
    }

    public static LongArgumentType longArg(final long min) {
        return longArg(min, Long.MAX_VALUE);
    }

    public static LongArgumentType longArg(final long min, final long max) {
        return new LongArgumentType(min, max);
    }

    public static long getLong(final CommandContext<?> context, final String name) {
        return context.getArgument(name, long.class);
    }

    public long getMinimum() {
        return minimum;
    }

    public long getMaximum() {
        return maximum;
    }

    @Override
    public Long parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final long result = reader.readLong();
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof LongArgumentType)) return false;

        final LongArgumentType that = (LongArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(minimum) + Long.hashCode(maximum);
    }

    @Override
    public String toString() {
        if (minimum == Long.MIN_VALUE && maximum == Long.MAX_VALUE) {
            return "longArg()";
        } else if (maximum == Long.MAX_VALUE) {
            return "longArg(" + minimum + ")";
        } else {
            return "longArg(" + minimum + ", " + maximum + ")";
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
