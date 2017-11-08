package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public interface ArgumentType<T> {
    <S> T parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException;

    default <S> CompletableFuture<Collection<String>> listSuggestions(final String command) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
}
