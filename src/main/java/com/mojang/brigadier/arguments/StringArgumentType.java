// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class StringArgumentType implements ArgumentType<String> {
    private final StringType type;

    private StringArgumentType(final StringType type) {
        this.type = type;
    }

    public static StringArgumentType word() {
        return new StringArgumentType(StringType.SINGLE_WORD);
    }

    public static StringArgumentType relaxedWord() {
        return new StringArgumentType(StringType.SINGLE_WORD_RELAXED);
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

    public StringType getType() {
        return type;
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        switch (type) {
            case SINGLE_WORD:
                return reader.readUnquotedString();
            case QUOTABLE_PHRASE:
                return reader.readString();
            case GREEDY_PHRASE:
                final String text = reader.getRemaining();
                reader.setCursor(reader.getTotalLength());
                return text;
            default:
                return reader.readUnquotedStringRelaxed();
        }
    }

    @Override
    public String toString() {
        return type.name;
    }

    @Override
    public Collection<String> getExamples() {
        return type.getExamples();
    }

    public static String escapeIfRequired(final String input) {
        for (final char c : input.toCharArray()) {
            if (!StringReader.isAllowedInUnquotedStringRelaxed(c)) {
                return escape(input);
            }
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

    public enum StringType {
        SINGLE_WORD("word()", "word", "words_with_underscores"),
        QUOTABLE_PHRASE("string()", "\"quoted phrase\"", "word", "wörd!", "\"\""),
        GREEDY_PHRASE("greedyString()", "word", "words with spaces", "\"and symbols\""),
        SINGLE_WORD_RELAXED("wordRelaxed()", "word", "wörd!");

        private final String name;
        private final Collection<String> examples;

        StringType(final String name, final String... examples) {
            this.name = name;
            this.examples = Arrays.asList(examples);
        }

        public String getName() {
            return name;
        }

        public Collection<String> getExamples() {
            return examples;
        }
    }
}
