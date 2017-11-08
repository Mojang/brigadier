package com.mojang.brigadier.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BoolArgumentType implements ArgumentType<Boolean> {
    private BoolArgumentType() {
    }

    public static BoolArgumentType bool() {
        return new BoolArgumentType();
    }

    public static boolean getBool(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Boolean.class);
    }

    @Override
    public <S> Boolean parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        return reader.readBoolean();
    }

    @Override
    public <S> CompletableFuture<Collection<String>> listSuggestions(final CommandContext<S> context, final String command) {
        final List<String> result = Lists.newArrayList();

        if ("true".startsWith(command)) {
            result.add("true");
        }
        if ("false".startsWith(command)) {
            result.add("false");
        }

        return CompletableFuture.completedFuture(result);
    }
}
