// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

/**
 * An {@link ArgumentType} that parses longs.
 * <p>
 * Allows for numbers in the following format:<br>
 * {@literal (-)?\d+}
 */
public class LongArgumentType implements ArgumentType<Long> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    private final long minimum;
    private final long maximum;

    private LongArgumentType(final long minimum, final long maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * A factory method as a simple way to get an instance.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", long())
     * </code>
     *
     *
     * @return an instance of this argument type
     */
    public static LongArgumentType longArg() {
        return longArg(Long.MIN_VALUE);
    }

    /**
     * A factory method as a simple way to get an instance of this class with an enforced minimum value.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", long(0))
     * </code>
     *
     * @param min the minimal value it needs to be in order to be a valid argument. Inclusive.
     * @return an instance of this argument type
     */
    public static LongArgumentType longArg(final long min) {
        return longArg(min, Long.MAX_VALUE);
    }

    /**
     * A factory method as a simple way to get an instance of this class with an enforced minimum and maximum value.
     * If you want to only define a maximum value, use {@code Long.MIN_VALUE} as minimum.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", long(0, 100))
     * </code>
     *
     * @param min the minimal value it needs to be in order to be a valid argument. Inclusive.
     * @param max the maximal value it needs to be in order to be a valid argument. Inclusive.
     * @return an instance of this argument type
     */
    public static LongArgumentType longArg(final long min, final long max) {
        return new LongArgumentType(min, max);
    }

    /**
     * Retrieves the argument with the given name from the context and casts it to a long.
     *
     * @param context the context to get the argument from, calls {@link CommandContext#getArgument}
     * @param name the name of the argument to retrieve
     * @return the argument as a long
     * @see CommandContext#getArgument
     */
    public static long getLong(final CommandContext<?> context, final String name) {
        return context.getArgument(name, long.class);
    }

    /**
     * The minimum value an argument is allowed to be (inclusive).
     *
     * @return the minimum value an argument is allowed to be (inclusive).
     */
    public long getMinimum() {
        return minimum;
    }

    /**
     * The maximal value an argument is allowed to be (inclusive).
     *
     * @return the maximal value an argument is allowed to be (inclusive).
     */
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
