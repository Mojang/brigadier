package com.mojang.brigadier;

import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class StringReader {
    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_QUOTE = '"';

    public static final SimpleCommandExceptionType ERROR_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType("parsing.quote.expected.start", "Expected quote to start a string");
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType("parsing.quote.expected.end", "Unclosed quoted string");
    public static final ParameterizedCommandExceptionType ERROR_INVALID_ESCAPE = new ParameterizedCommandExceptionType("parsing.quote.escape", "Invalid escape sequence '\\${character}' in quoted string)", "character");
    public static final ParameterizedCommandExceptionType ERROR_INVALID_BOOL = new ParameterizedCommandExceptionType("parsing.bool.invalid", "Invalid bool, expected true or false but found '${value}'", "value");
    public static final ParameterizedCommandExceptionType ERROR_INVALID_INT = new ParameterizedCommandExceptionType("parsing.int.invalid", "Invalid integer '${value}'", "value");
    public static final SimpleCommandExceptionType ERROR_EXPECTED_INT = new SimpleCommandExceptionType("parsing.int.expected", "Expected integer");
    public static final ParameterizedCommandExceptionType ERROR_INVALID_DOUBLE = new ParameterizedCommandExceptionType("parsing.double.invalid", "Invalid double '${value}'", "value");
    public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE = new SimpleCommandExceptionType("parsing.double.expected", "Expected double");

    private final String string;
    private int cursor;

    public StringReader(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public int getRemainingLength() {
        return string.length() - cursor;
    }

    public int getTotalLength() {
        return string.length();
    }

    public int getCursor() {
        return cursor;
    }

    public String getRead() {
        return string.substring(0, cursor);
    }

    public String getRemaining() {
        return string.substring(cursor);
    }

    public boolean canRead(final int length) {
        return cursor + length <= string.length();
    }

    public boolean canRead() {
        return canRead(1);
    }

    public char peek() {
        return string.charAt(cursor);
    }

    public char read() {
        return string.charAt(cursor++);
    }

    public void skip() {
        cursor++;
    }

    private static boolean isAllowedNumber(final char c) {
        return c >= '0' && c <= '9' || c == '.' || c == '-';
    }

    public int readInt() throws CommandException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw ERROR_EXPECTED_INT.create();
        }
        try {
            return Integer.parseInt(number);
        } catch (final NumberFormatException ex) {
            throw ERROR_INVALID_INT.create(number);
        }
    }

    public double readDouble() throws CommandException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw ERROR_EXPECTED_DOUBLE.create();
        }
        try {
            return Double.parseDouble(number);
        } catch (final NumberFormatException ex) {
            throw ERROR_INVALID_DOUBLE.create(number);
        }
    }

    private static boolean isAllowedInUnquotedString(final char c) {
        return c >= '0' && c <= '9'
            || c >= 'A' && c <= 'Z'
            || c >= 'a' && c <= 'z'
            || c == '_' || c == '-'
            || c == '.' || c == '+';
    }

    public String readUnquotedString() throws CommandException {
        final int start = cursor;
        while (canRead() && isAllowedInUnquotedString(peek())) {
            skip();
        }
        return string.substring(start, cursor);
    }

    public String readQuotedString() throws CommandException {
        if (!canRead()) {
            return "";
        } else if (peek() != SYNTAX_QUOTE) {
            throw ERROR_EXPECTED_START_OF_QUOTE.create();
        }
        skip();
        final StringBuilder result = new StringBuilder();
        boolean escaped = false;
        while (canRead()) {
            final char c = read();
            if (escaped) {
                if (c == SYNTAX_QUOTE || c == SYNTAX_ESCAPE) {
                    result.append(c);
                    escaped = false;
                } else {
                    throw ERROR_INVALID_ESCAPE.create(String.valueOf(c));
                }
            } else if (c == SYNTAX_ESCAPE) {
                escaped = true;
            } else if (c == SYNTAX_QUOTE) {
                return result.toString();
            } else {
                result.append(c);
            }
        }

        throw ERROR_EXPECTED_END_OF_QUOTE.create();
    }

    public String readString() throws CommandException {
        if (canRead() && peek() == SYNTAX_QUOTE) {
            return readQuotedString();
        } else {
            return readUnquotedString();
        }
    }

    public boolean readBoolean() throws CommandException {
        String value = readString();
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            throw ERROR_INVALID_BOOL.create(value);
        }
    }
}
