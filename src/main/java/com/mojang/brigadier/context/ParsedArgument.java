// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import java.util.Objects;

public class ParsedArgument<S, T> {
    private final StringRange range;
    private final T result;

    public ParsedArgument(final int start, final int end, final T result) {
        this.range = StringRange.between(start, end);
        this.result = result;
    }

    public StringRange getRange() {
        return range;
    }

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
