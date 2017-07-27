package com.mojang.brigadier.context;

public class ParsedArgument<S, T> {
    private final String raw;
    private final T result;

    public ParsedArgument(final String raw, final T result) {
        this.raw = raw;
        this.result = result;
    }

    public String getRaw() {
        return raw;
    }

    public T getResult() {
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ParsedArgument)) return false;

        final ParsedArgument that = (ParsedArgument) o;

        if (!raw.equals(that.raw)) return false;
        if (!result.equals(that.result)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = raw.hashCode();
        result = 31 * result + this.result.hashCode();
        return result;
    }

}
