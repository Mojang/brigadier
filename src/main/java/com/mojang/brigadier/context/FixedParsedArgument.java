package com.mojang.brigadier.context;

public class FixedParsedArgument<S, T> implements ParsedArgument<S, T> {
    private final String raw;
    private final T result;

    public FixedParsedArgument(String raw, T result) {
        this.raw = raw;
        this.result = result;
    }

    @Override
    public String getRaw() {
        return raw;
    }

    @Override
    public T getResult(S source) {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixedParsedArgument)) return false;

        FixedParsedArgument that = (FixedParsedArgument) o;

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

    @Override
    public ParsedArgument<S, T> copy() {
        return new FixedParsedArgument<>(raw, result);
    }
}
