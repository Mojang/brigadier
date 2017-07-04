package com.mojang.brigadier.context;

public interface ParsedArgument<S, T> {
    String getRaw();

    T getResult(S source);

    ParsedArgument<S, T> copy();
}
