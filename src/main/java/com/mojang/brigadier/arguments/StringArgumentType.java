package com.mojang.brigadier.arguments;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;

public class StringArgumentType implements ArgumentType<String> {
    private final StringType type;

    private StringArgumentType(final StringType type) {
        this.type = type;
    }

    public static StringArgumentType word() {
        return new StringArgumentType(StringType.SINGLE_WORD);
    }

    public static StringArgumentType string() {
        return new StringArgumentType(StringType.QUOTABLE_PHRASE);
    }

    public static StringArgumentType greedyString() {
        return new StringArgumentType(StringType.GREEDY_PHRASE);
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> String parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandException {
        if (type == StringType.GREEDY_PHRASE) {
            final String text = reader.getRemaining();
            reader.setCursor(reader.getTotalLength());
            return text;
        } else if (type == StringType.SINGLE_WORD) {
            return reader.readUnquotedString();
        } else {
            return reader.readString();
        }
    }

    @Override
    public String toString() {
        return "string()";
    }

    public static String escapeIfRequired(final String input) {
        if (input.contains("\\") || input.contains("\"") || input.contains(CommandDispatcher.ARGUMENT_SEPARATOR)) {
            return escape(input);
        }
        return input;
    }

    private static String escape(final String input) {
        final StringBuilder result = new StringBuilder("\"");

        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c == '\\' || c == '"') {
                result.append('\\');
            }
            result.append(c);
        }

        result.append("\"");
        return result.toString();
    }

    @Override
    public String getUsageText() {
        return "string";
    }

    public enum StringType {
        SINGLE_WORD,
        QUOTABLE_PHRASE,
        GREEDY_PHRASE,
    }
}
