package com.mojang.brigadier.context;

public interface ParsedArgument<T> {
    String getRaw();

    T getResult();

    ParsedArgument<T> copy();
}
