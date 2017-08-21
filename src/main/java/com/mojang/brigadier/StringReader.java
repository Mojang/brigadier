package com.mojang.brigadier;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class StringReader implements ImmutableStringReader {
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
    public static final SimpleCommandExceptionType ERROR_EXPECTED_BOOL = new SimpleCommandExceptionType("parsing.bool.expected", "Expected bool");
    public static final ParameterizedCommandExceptionType ERROR_EXPECTED_SYMBOL = new ParameterizedCommandExceptionType("parsing.expected", "Expected '${symbol}'", "symbol");

    private final String string;
    private int cursor;

    public StringReader(final String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    public void setCursor(final int cursor) {
        this.cursor = cursor;
    }

    @Override
    public int getRemainingLength() {
        return string.length() - cursor;
    }

    @Override
    public int getTotalLength() {
        return string.length();
    }

    @Override
    public int getCursor() {
        return cursor;
    }

    @Override
    public String getRead() {
        return string.substring(0, cursor);
    }

    @Override
    public String getRemaining() {
        return string.substring(cursor);
    }

    @Override
    public boolean canRead(final int length) {
        return cursor + length <= string.length();
    }

    @Override
    public boolean canRead() {
        return canRead(1);
    }

    @Override
    public char peek() {
        return string.charAt(cursor);
    }

    @Override
    public char peek(final int offset) {
        return string.charAt(cursor + offset);
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

    public void skipWhitespace() {
        while (canRead() && Character.isWhitespace(peek())) {
            skip();
        }
    }

    public int readInt() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw ERROR_EXPECTED_INT.createWithContext(this);
        }
        try {
            return Integer.parseInt(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw ERROR_INVALID_INT.createWithContext(this, number);
        }
    }

    public double readDouble() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw ERROR_EXPECTED_DOUBLE.createWithContext(this);
        }
        try {
            return Double.parseDouble(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw ERROR_INVALID_DOUBLE.createWithContext(this, number);
        }
    }

    private static boolean isAllowedInUnquotedString(final char c) {
        return c >= '0' && c <= '9'
            || c >= 'A' && c <= 'Z'
            || c >= 'a' && c <= 'z'
            || c == '_' || c == '-'
            || c == '.' || c == '+';
    }

    public String readUnquotedString() {
        final int start = cursor;
        while (canRead() && isAllowedInUnquotedString(peek())) {
            skip();
        }
        return string.substring(start, cursor);
    }

    public String readQuotedString() throws CommandSyntaxException {
        if (!canRead()) {
            return "";
        } else if (peek() != SYNTAX_QUOTE) {
            throw ERROR_EXPECTED_START_OF_QUOTE.createWithContext(this);
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
                    setCursor(getCursor() - 1);
                    throw ERROR_INVALID_ESCAPE.createWithContext(this, String.valueOf(c));
                }
            } else if (c == SYNTAX_ESCAPE) {
                escaped = true;
            } else if (c == SYNTAX_QUOTE) {
                return result.toString();
            } else {
                result.append(c);
            }
        }

        throw ERROR_EXPECTED_END_OF_QUOTE.createWithContext(this);
    }

    public String readString() throws CommandSyntaxException {
        if (canRead() && peek() == SYNTAX_QUOTE) {
            return readQuotedString();
        } else {
            return readUnquotedString();
        }
    }

    public boolean readBoolean() throws CommandSyntaxException {
        final int start = cursor;
        final String value = readString();
        if (value.isEmpty()) {
            throw ERROR_EXPECTED_BOOL.createWithContext(this);
        }

        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            cursor = start;
            throw ERROR_INVALID_BOOL.createWithContext(this, value);
        }
    }

    public void expect(final char c) throws CommandSyntaxException {
        if (!canRead() || peek() != c) {
            throw ERROR_EXPECTED_SYMBOL.createWithContext(this, String.valueOf(c));
        }
        skip();
    }
}
