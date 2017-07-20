package com.mojang.brigadier.arguments;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.FixedParsedArgument;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Set;

public class StringArgumentType implements ArgumentType<String> {
    public static final ParameterizedCommandExceptionType ERROR_INVALID_ESCAPE = new ParameterizedCommandExceptionType("argument.string.escape.invalid", "Unknown or invalid escape sequence: ${input}", "input");
    public static final SimpleCommandExceptionType ERROR_UNEXPECTED_ESCAPE = new SimpleCommandExceptionType("argument.string.escape.unexpected", "Unexpected escape sequence, please quote the whole argument");
    public static final SimpleCommandExceptionType ERROR_UNEXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType("argument.string.quote.unexpected_start", "Unexpected start-of-quote character (\"), please quote the whole argument");
    public static final SimpleCommandExceptionType ERROR_UNEXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType("argument.string.quote.unexpected_end", "Unexpected end-of-quote character (\"), it must be at the end or followed by a space (' ') for the next argument");
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType("argument.string.quote.expected_end", "Expected end-of-quote character (\") but found no more input");
    private final StringType type;

    private StringArgumentType(StringType type) {
        this.type = type;
    }

    public static StringArgumentType word() {
        return new StringArgumentType(StringType.SINGLE_WORLD);
    }

    public static StringArgumentType string() {
        return new StringArgumentType(StringType.QUOTABLE_PHRASE);
    }

    public static StringArgumentType greedyString() {
        return new StringArgumentType(StringType.GREEDY_PHRASE);
    }

    public static String getString(CommandContext<?> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> ParsedArgument<S, String> parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        if (type == StringType.GREEDY_PHRASE) {
            return new FixedParsedArgument<>(command, command);
        } else if (type == StringType.SINGLE_WORLD) {
            int index = command.indexOf(CommandDispatcher.ARGUMENT_SEPARATOR);
            if (index > 0) {
                final String word = command.substring(0, index);
                return new FixedParsedArgument<>(word, word);
            } else {
                return new FixedParsedArgument<>(command, command);
            }
        } else {
            StringBuilder result = new StringBuilder();
            int i = 0;
            boolean escaped = false;
            boolean quoted = false;
            while (i < command.length()) {
                char c = command.charAt(i);
                if (escaped) {
                    if (c == '"' || c == '\\') {
                        result.append(c);
                    } else {
                        throw ERROR_INVALID_ESCAPE.create("\\" + c);
                    }
                    escaped = false;
                } else if (c == '\\') {
                    if (quoted) {
                        escaped = true;
                    } else {
                        throw ERROR_UNEXPECTED_ESCAPE.create();
                    }
                } else if (c == '"') {
                    if (i == 0) {
                        quoted = true;
                    } else if (!quoted) {
                        throw ERROR_UNEXPECTED_START_OF_QUOTE.create();
                    } else if (i == command.length() - 1 || command.charAt(i + 1) == CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                        i++;
                        break;
                    } else {
                        throw ERROR_UNEXPECTED_END_OF_QUOTE.create();
                    }
                } else if (!quoted && c == CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                    break;
                } else if (quoted && i == command.length() - 1) {
                    throw ERROR_EXPECTED_END_OF_QUOTE.create();
                } else {
                    result.append(c);
                }

                i++;
            }
            return new FixedParsedArgument<>(command.substring(0, i), result.toString());
        }
    }

    @Override
    public String toString() {
        return "string()";
    }

    public static String escapeIfRequired(String input) {
        if (input.contains("\\") || input.contains("\"") || input.contains(CommandDispatcher.ARGUMENT_SEPARATOR)) {
            return escape(input);
        }
        return input;
    }

    private static String escape(String input) {
        StringBuilder result = new StringBuilder("\"");

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
        SINGLE_WORLD,
        QUOTABLE_PHRASE,
        GREEDY_PHRASE,
    }
}
