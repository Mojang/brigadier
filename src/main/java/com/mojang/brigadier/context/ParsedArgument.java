// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import java.util.Objects;

/**
 * Represents an argument that was parsed from the input.
 *
 * @param <S> the type of the command source
 * @param <T> the type of the argument
 */
public class ParsedArgument<S, T> {
    private final StringRange range;
    private final T result;

    /**
     * Creates a new {@link ParsedArgument} for a given string range with a  given value.
     *
     * @param start the start of this argument in the input
     * @param end the end of this argument in the input
     * @param result the value of this argument
     */
    public ParsedArgument(final int start, final int end, final T result) {
        this.range = StringRange.between(start, end);
        this.result = result;
    }

    /**
     * Returns the range this argument spans in the input string.
     *
     * @return the range this argument spans in the input string
     */
    public StringRange getRange() {
        return range;
    }

    /**
     * Returns the value of the argument.
     *
     * @return the value of the argument
     */
    public T getResult() {
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParsedArgument)) {
            return false;
        }
        final ParsedArgument<?, ?> that = (ParsedArgument<?, ?>) o;
        return Objects.equals(range, that.range) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, result);
    }
}
