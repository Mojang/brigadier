// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.Collections;

public class LiteralArgumentType implements ArgumentType<String> {

    private static final LiteralArgumentType INSTANCE = new LiteralArgumentType();

    private LiteralArgumentType() {
    }

    public static LiteralArgumentType literal() {
        return INSTANCE;
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        return reader.getString().substring(start, reader.getCursor());
    }

    @Override
    public String toString() {
        return "literal_arg()";
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.emptyList();
    }

}
