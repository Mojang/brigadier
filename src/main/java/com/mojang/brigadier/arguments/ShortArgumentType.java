// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class ShortArgumentType implements ArgumentType<Short> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    private final short minimum;
    private final short maximum;

    private ShortArgumentType(final short minimum, final short maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static ShortArgumentType shortArg() {
        return shortArg(Short.MIN_VALUE);
    }

    public static ShortArgumentType shortArg(final short min) {
        return shortArg(min, Short.MAX_VALUE);
    }

    public static ShortArgumentType shortArg(final short min, final short max) {
        return new ShortArgumentType(min, max);
    }

    public static short getShort(final CommandContext<?> context, final String name) {
        return context.getArgument(name, short.class);
    }

    public short getMinimum() {
        return minimum;
    }

    public short getMaximum() {
        return maximum;
    }

    @Override
    public Short parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final short result = reader.readShort();
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.shortTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.shortTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortArgumentType)) return false;

        final ShortArgumentType that = (ShortArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return 31 * minimum + maximum;
    }

    @Override
    public String toString() {
        if (minimum == Short.MIN_VALUE && maximum == Short.MAX_VALUE) {
            return "shortArg()";
        } else if (maximum == Short.MAX_VALUE) {
            return "shortArg(" + minimum + ")";
        } else {
            return "shortArg(" + minimum + ", " + maximum + ")";
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
