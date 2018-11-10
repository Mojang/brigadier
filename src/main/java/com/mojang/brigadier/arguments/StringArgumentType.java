// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

/**
 * An {@link ArgumentType} that parses strings.
 */
public class StringArgumentType implements ArgumentType<String> {
    private final StringType type;

    private StringArgumentType(final StringType type) {
        this.type = type;
    }

    /**
     * An instance of this argument type that only parses a single word, i.e. up until a space.
     *
     * @return an instance of this argument type parsing a single word.
     */
    public static StringArgumentType word() {
        return new StringArgumentType(StringType.SINGLE_WORD);
    }

    /**
     * An instance of this argument type that only parses a single word or a quoted string.
     *
     * @return an instance of this argument type parsing a word or quoted string.
     */
    public static StringArgumentType string() {
        return new StringArgumentType(StringType.QUOTABLE_PHRASE);
    }

    /**
     * An instance of this argument type that only parses everything up until the end of the input.
     *
     * @return an instance of this argument type parsing everything until the end of the input
     */
    public static StringArgumentType greedyString() {
        return new StringArgumentType(StringType.GREEDY_PHRASE);
    }

    /**
     * Retrieves the argument with the given name from the context and casts it to an string.
     *
     * @param context the context to get the argument from, calls {@link CommandContext#getArgument}
     * @param name the name of the argument to retrieve
     * @return the argument as an string
     * @see CommandContext#getArgument
     */
    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    /**
     * The type of string this argument parses.
     *
     * @return the {@link StringType} this argument parses
     */
    public StringType getType() {
        return type;
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
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

    @Override
    public Collection<String> getExamples() {
        return type.getExamples();
    }

    /**
     * Converts a String to phrase {@link StringType#QUOTABLE_PHRASE} would match.
     * <p>
     * This will escape all characters that can not appear in an unquoted and wrap the string in quotes, if required.
     * More specifically, this will always wrap the string in quotes, if <em>any</em> character in it needs to be escaped.
     * <p>
     * <p>
     * Some sample in and outputs:
     * <ul>
     * <li>{@code hey} to {@code hey}</li>
     * <li>{@code hey you} to {@code "hey you"}</li>
     * <li>{@code "hello"} to {@code "\"hello\""}</li>
     * <li>{@code \} to {@code "\\"}</li>
     * </ul>
     *
     * @param input the input to escape
     * @return the escaped output ready to be parsed by {@link StringType#QUOTABLE_PHRASE}
     */
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

    /**
     * The type of phrase that is matched by {@link StringArgumentType}.
     */
    public enum StringType {
        /**
         * Matches a single word, optionally concatenated with underscores.
         * Can contain the following characters: <br>
         * {@code [a-zA-Z0-9_\-.+]}
         */
        SINGLE_WORD("word", "words_with_underscores"),
        /**
         * Matches what {@link #SINGLE_WORD} matches or a phrase surrounded by quotes ({@literal "}), optionally
         * containing escaped characters.
         */
        QUOTABLE_PHRASE("\"quoted phrase\"", "word", "\"\""),
        /**
         * Just greedily matches the whole available input.
         */
        GREEDY_PHRASE("word", "words with spaces", "\"and symbols\""),
        ;

        private final Collection<String> examples;

        StringType(final String... examples) {
            this.examples = Arrays.asList(examples);
        }

        /**
         * Returns some examples that define this type, which are used to detect ambiguities.
         *
         * @return some examples that define this type, which are used to detect ambiguities.
         * @see ArgumentType#getExamples()
         */
        public Collection<String> getExamples() {
            return examples;
        }
    }
}
