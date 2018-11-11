// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

/**
 * An {@link ArgumentType} that parses integers.
 * <p>
 * Allows for numbers in the following format:<br>
 * {@literal (-)?\d+}
 */
public class IntegerArgumentType implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    private final int minimum;
    private final int maximum;

    private IntegerArgumentType(final int minimum, final int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * A factory method as a simple way to get an instance.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", integer())
     * </code>
     *
     * @return an instance of this argument type
     */
    public static IntegerArgumentType integer() {
        return integer(Integer.MIN_VALUE);
    }

    /**
     * A factory method as a simple way to get an instance of this class with an enforced minimum value.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", integer(0))
     * </code>
     *
     * @param min the minimal value it needs to be in order to be a valid argument. Inclusive.
     * @return an instance of this argument type
     */
    public static IntegerArgumentType integer(final int min) {
        return integer(min, Integer.MAX_VALUE);
    }

    /**
     * A factory method as a simple way to get an instance of this class with an enforced minimum and maximum value.
     * If you want to only define a maximum value, use {@code Integer.MIN_VALUE} as minimum.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", integer(0, 100))
     * </code>
     *
     * @param min the minimal value it needs to be in order to be a valid argument. Inclusive.
     * @param max the maximal value it needs to be in order to be a valid argument. Inclusive.
     * @return an instance of this argument type
     */
    public static IntegerArgumentType integer(final int min, final int max) {
        return new IntegerArgumentType(min, max);
    }

    /**
     * Retrieves the argument with the given name from the context and casts it to an integer.
     *
     * @param context the context to get the argument from, calls {@link CommandContext#getArgument}
     * @param name the name of the argument to retrieve
     * @return the argument as an integer
     * @see CommandContext#getArgument
     */
    public static int getInteger(final CommandContext<?> context, final String name) {
        return context.getArgument(name, int.class);
    }

    /**
     * The minimum value an argument is allowed to be (inclusive).
     *
     * @return the minimum value an argument is allowed to be (inclusive).
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * The maximal value an argument is allowed to be (inclusive).
     *
     * @return the maximal value an argument is allowed to be (inclusive).
     */
    public int getMaximum() {
        return maximum;
    }

    @Override
    public Integer parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int result = reader.readInt();
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerArgumentType)) return false;

        final IntegerArgumentType that = (IntegerArgumentType) o;
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

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
