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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StringArgumentType implements ArgumentType<String> {
    private final StringType type;
    private final Set<String> options;

    private StringArgumentType(final StringType type) {
        this.type = type;
        this.options = null;
    }

    private StringArgumentType(final StringType type, Collection<String> options) {
        this.type = type;
        this.options = new HashSet<>(options);
    }

    public static StringArgumentType word() {
        return new StringArgumentType(StringType.SINGLE_WORD);
    }

    public static StringArgumentType term(Collection<String> options) {
        return new StringArgumentType(StringType.TERM, options);
    }

    public static StringArgumentType term(String ... options) {
        return term(Arrays.asList(options));
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
        if (type == StringType.GREEDY_PHRASE) {
            final String text = reader.getRemaining();
            reader.setCursor(reader.getTotalLength());
            return text;
        } else if (type == StringType.SINGLE_WORD) {
            return reader.readUnquotedString();
        } else if (type == StringType.TERM) {
            final String term = reader.readUnquotedString();
            if (!options.contains(term)) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidString().createWithContext(reader, term);
            }
            return term;
        }
        else {
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

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (type != StringType.TERM) {
            return Suggestions.empty();
        }
        options.stream().filter((s) -> s.startsWith(builder.getRemaining().toLowerCase())).map(builder::suggest);
        return builder.buildFuture();
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
        GREEDY_PHRASE("word", "words with spaces", "\"and symbols\""),
        TERM("foo bar baz", "fixed collection of terms"),;

        private final Collection<String> examples;

        StringType(final String... examples) {
            this.examples = Arrays.asList(examples);
        }

        public Collection<String> getExamples() {
            return examples;
        }
    }
}
