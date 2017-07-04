package com.mojang.brigadier.context;

import java.util.function.Function;

public class DynamicParsedArgument<S, T> implements ParsedArgument<S, T> {
    private final String raw;
    private Function<S, T> supplier;
    private boolean evaluated;
    private T result;

    public DynamicParsedArgument(String raw, Function<S, T> supplier) {
        this.raw = raw;
        this.supplier = supplier;
    }

    @Override
    public String getRaw() {
        return raw;
    }

    @Override
    public T getResult(S source) {
        if (!evaluated) {
            result = supplier.apply(source);
            evaluated = true;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DynamicParsedArgument)) return false;

        DynamicParsedArgument that = (DynamicParsedArgument) o;

        if (!raw.equals(that.raw)) return false;
        if (!supplier.equals(that.supplier)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = raw.hashCode();
        result = 31 * result + supplier.hashCode();
        return result;
    }

    @Override
    public ParsedArgument<S, T> copy() {
        return new DynamicParsedArgument<>(raw, supplier);
    }
}
