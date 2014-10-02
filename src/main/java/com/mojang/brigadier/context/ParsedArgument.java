package com.mojang.brigadier.context;

public class ParsedArgument<T> {
    private final String raw;
    private final T result;

    public ParsedArgument(String raw, T result) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParsedArgument)) return false;

        ParsedArgument that = (ParsedArgument) o;

        if (!raw.equals(that.raw)) return false;
        if (!result.equals(that.result)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = raw.hashCode();
        result1 = 31 * result1 + result.hashCode();
        return result1;
    }
}
