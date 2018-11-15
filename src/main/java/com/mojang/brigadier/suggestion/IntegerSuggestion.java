// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;

import java.util.Objects;

/**
 * A {@link Suggestion} that suggests an integer.
 * The method {@link #compareTo(Suggestion)} orders IntegerSuggestions in ascending order, but when compared
 * with other suggestion types orders them based on the textual representation of the integer value.
 */
public class IntegerSuggestion extends Suggestion {
    private int value;

    /**
     * Creates a new IntegerSuggestion that covers a given range and suggests a given int value.
     *
     * @param range the range it covers in the input
     * @param value the integer value to suggest
     */
    public IntegerSuggestion(final StringRange range, final int value) {
        this(range, value, null);
    }

    /**
     * Creates a new IntegerSuggestion that covers a given range and has a given int value and tooltip.
     *
     * @param range the range it covers in the input
     * @param value the integer value to suggest
     * @param tooltip a tooltip message to show the user
     */
    public IntegerSuggestion(final StringRange range, final int value, final Message tooltip) {
        super(range, Integer.toString(value), tooltip);
        this.value = value;
    }

    /**
     * Returns the underlying integer suggestion value.
     *
     * @return the underlying integer suggestion value
     */
    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerSuggestion)) {
            return false;
        }
        final IntegerSuggestion that = (IntegerSuggestion) o;
        return value == that.value && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return "IntegerSuggestion{" +
            "value=" + value +
            ", range=" + getRange() +
            ", text='" + getText() + '\'' +
            ", tooltip='" + getTooltip() + '\'' +
            '}';
    }

    @Override
    public int compareTo(final Suggestion o) {
        if (o instanceof IntegerSuggestion) {
            return Integer.compare(value, ((IntegerSuggestion) o).value);
        }
        return super.compareTo(o);
    }

    @Override
    public int compareToIgnoreCase(final Suggestion b) {
        return compareTo(b);
    }
}
