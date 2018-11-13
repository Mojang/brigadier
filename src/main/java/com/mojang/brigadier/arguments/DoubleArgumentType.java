// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

// @formatter:off
/**
 * An {@link ArgumentType} that parses doubles.
 * <p>
 * Allows for numbers like the following:
 * <ul>
 *     <li>{@code 1.2}</li>
 *     <li>{@code 0}</li>
 *     <li>{@code .5}</li>
 *     <li>{@code -.5}</li>
 *     <li>{@code -1}</li>
 *     <li>{@code -132323.4242}</li>
 * </ul>
 */
// @formatter:on
public class DoubleArgumentType implements ArgumentType<Double> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");

    private final double minimum;
    private final double maximum;

    private DoubleArgumentType(final double minimum, final double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * A factory method as a simple way to get an instance.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", doubleArg())
     * </code>
     *
     * @return an instance of this argument type
     */
    public static DoubleArgumentType doubleArg() {
        return doubleArg(-Double.MAX_VALUE);
    }

    /**
     * A factory method as a simple way to get an instance of this class with an enforced minimum value.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", doubleArg(0))
     * </code>
     *
     * @param min the minimal value for the double to be considered valid. Inclusive
     * @return an instance of this argument type
     */
    public static DoubleArgumentType doubleArg(final double min) {
        return doubleArg(min, Double.MAX_VALUE);
    }

    /**
     * A factory method as a simple way to get an instance of this class with an enforced minimum and maximum value.
     * If you want to only define a maximum value, use {@code -Double.MAX_VALUE} as minimum.
     * <p>
     * It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", doubleArg(0, 100))
     * </code>
     *
     * @param min the minimal value for the double to be considered valid. Inclusive
     * @param max the maximal value it needs to be in order to be a valid argument. Inclusive
     * @return an instance of this argument type
     */
    public static DoubleArgumentType doubleArg(final double min, final double max) {
        return new DoubleArgumentType(min, max);
    }

    /**
     * Retrieves the argument with the given name from the context and casts it to a double.
     *
     * @param context the context to get the argument from, calls {@link CommandContext#getArgument}
     * @param name the name of the argument to retrieve
     * @return the argument as a double
     * @see CommandContext#getArgument
     */
    public static double getDouble(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Double.class);
    }

    /**
     * The minimum value an argument is allowed to be (inclusive).
     *
     * @return the minimum value an argument is allowed to be (inclusive)
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * The maximal value an argument is allowed to be (inclusive).
     *
     * @return the maximal value an argument is allowed to be (inclusive)
     */
    public double getMaximum() {
        return maximum;
    }

    @Override
    public Double parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final double result = reader.readDouble();
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleArgumentType)) return false;

        final DoubleArgumentType that = (DoubleArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return (int) (31 * minimum + maximum);
    }

    @Override
    public String toString() {
        if (minimum == -Double.MAX_VALUE && maximum == Double.MAX_VALUE) {
            return "double()";
        } else if (maximum == Double.MAX_VALUE) {
            return "double(" + minimum + ")";
        } else {
            return "double(" + minimum + ", " + maximum + ")";
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
