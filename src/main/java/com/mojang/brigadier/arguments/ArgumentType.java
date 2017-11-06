package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Set;

public interface ArgumentType<T> {
    <S> T parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException;

    default <S> void listSuggestions(final String command, final Set<String> output, final CommandContextBuilder<S> contextBuilder) {
    }

    default String getUsageSuffix() {
        return null;
    }

}
