// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * An {@link ArgumentType} that parses booleans.
 */
public class BoolArgumentType implements ArgumentType<Boolean> {
    private static final Collection<String> EXAMPLES = Arrays.asList("true", "false");

    private BoolArgumentType() {
    }

    /**
     * A factory method as a simple way to get an instance.
     *
     * <p>It is recommended to statically import this method to provide an interface similar to:<br>
     * <code>
     * argument("name", bool())
     * </code>
     * </p>
     *
     * @return an instance of this class
     */
    public static BoolArgumentType bool() {
        return new BoolArgumentType();
    }

    /**
     * Retrieves the argument with the given name from the context and casts it to a boolean.
     *
     * @param context the context to get the argument from, calls {@link CommandContext#getArgument}
     * @param name the name of the argument to retrieve
     * @return the argument as a boolean
     * @see CommandContext#getArgument
     */
    public static boolean getBool(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Boolean.class);
    }

    @Override
    public Boolean parse(final StringReader reader) throws CommandSyntaxException {
        return reader.readBoolean();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if ("true".startsWith(builder.getRemaining().toLowerCase())) {
            builder.suggest("true");
        }
        if ("false".startsWith(builder.getRemaining().toLowerCase())) {
            builder.suggest("false");
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
