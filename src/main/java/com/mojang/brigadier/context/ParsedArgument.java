package com.mojang.brigadier.context;

import com.mojang.brigadier.ImmutableStringReader;

public class ParsedArgument<S, T> {
    private final int start;
    private final int end;
    private final T result;

    public ParsedArgument(final int start, final int end, final T result) {
        this.start = start;
        this.end = end;
        this.result = result;
    }

    public String getRaw(final ImmutableStringReader reader) {
        return reader.getString().substring(start, end);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public T getResult() {
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ParsedArgument)) return false;

        final ParsedArgument that = (ParsedArgument) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        if (!result.equals(that.result)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + this.result.hashCode();
        return result;
    }

}
