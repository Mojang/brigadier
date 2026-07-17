// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class StringArgumentType implements ArgumentType<String> {
    private final boolean greedyCharset;
    private final StringType type;

    private StringArgumentType(final StringType type, final boolean greedyCharset) {
        this.type = type;
        this.greedyCharset = greedyCharset;
    }

    public static StringArgumentType word() {
        return word(false);
    }

    public static StringArgumentType word(boolean greedyCharset) {
        return new StringArgumentType(StringType.SINGLE_WORD, greedyCharset);
    }

    public static StringArgumentType string() {
        return string(false);
    }

    public static StringArgumentType string(boolean greedyCharset) {
        return new StringArgumentType(StringType.QUOTABLE_PHRASE, greedyCharset);
    }

    public static StringArgumentType greedyString() {
        return new StringArgumentType(StringType.GREEDY_PHRASE, false);
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    public StringType getType() {
        return type;
    }

    public boolean hasGreedyCharset() {
        return greedyCharset;
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        if (type == StringType.GREEDY_PHRASE) {
            final String text = reader.getRemaining();
            reader.setCursor(reader.getTotalLength());
            return text;
        } else if (type == StringType.SINGLE_WORD) {
            if (this.greedyCharset) {
                return reader.readUnquotedStringGreedy();
            } else {
                return reader.readUnquotedString();
            }
        } else {
            if (this.greedyCharset) {
                return reader.readStringGreedy();
            } else {
                return reader.readString();
            }
        }
    }

    @Override
    public String toString() {
        return "string()";
    }

    @Override
    public Collection<String> getExamples() {
        return type.getExamples();
    }

    public static String escapeIfRequired(final String input) {
        for (final char c : input.toCharArray()) {
            if (!StringReader.isAllowedInUnquotedString(c)) {
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
        SINGLE_WORD("word", "words_with_underscores"),
        QUOTABLE_PHRASE("\"quoted phrase\"", "word", "\"\""),
        GREEDY_PHRASE("word", "words with spaces", "\"and symbols\""),;

        private final Collection<String> examples;

        StringType(final String... examples) {
            this.examples = Arrays.asList(examples);
        }

        public Collection<String> getExamples() {
            return examples;
        }
    }
}
