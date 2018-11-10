// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

/**
 * An {@link ArgumentType} that parses doubles.
 * <p>
 * Allows for numbers in the following format:<br>
 * {@literal (-)?\d+('.'\d+)*}
 */
public class DoubleArgumentType implements ArgumentType<Double> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");

    private final double minimum;
    private final double maximum;

    private DoubleArgumentType(final double minimum, final double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * A factory method intended for use via a static import.
     *
     * @return an instance of this argument type
     */
    public static DoubleArgumentType doubleArg() {
        return doubleArg(-Double.MAX_VALUE);
    }

    /**
     * A factory method intended for use via a static import which enforces a minimum value.
     *
     * @param min the minimal value it needs to be in order to be a valid argument. Inclusive.
     * @return an instance of this argument type
     */
    public static DoubleArgumentType doubleArg(final double min) {
        return doubleArg(min, Double.MAX_VALUE);
    }

    /**
     * A factory method intended for use via a static import which enforces that the number lies within a given range.
     *
     * @param min the minimal value it needs to be in order to be a valid argument. Inclusive.
     * @param max the maximal value it needs to be in order to be a valid argument. Inclusive.
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
     * @return the minimum value an argument is allowed to be (inclusive).
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * The maximal value an argument is allowed to be (inclusive).
     *
     * @return the maximal value an argument is allowed to be (inclusive).
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
