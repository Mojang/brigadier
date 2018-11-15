// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * A mutable implementation of an {@link ImmutableStringReader}, that allows moving the cursor.
 * This is done by calling some of the methods that consume some part of the input string (like {@link #readInt}), as
 * they advance the cursor behind the input they have read.
 *
 * <p>
 * This class also provides methods to freely set the cursor.
 */
public class StringReader implements ImmutableStringReader {
    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_QUOTE = '"';

    private final String string;
    private int cursor;

    public StringReader(final StringReader other) {
        this.string = other.string;
        this.cursor = other.cursor;
    }

    public StringReader(final String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    /**
     * Sets the cursor index position.
     *
     * @param cursor the new cursor position
     */
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

    /**
     * Reads the next character.
     * <p>
     * Same as {@link #peek()}, but also consumes the character.
     *
     * @return the read character
     */
    public char read() {
        return string.charAt(cursor++);
    }

    /**
     * Skips a single character.
     */
    public void skip() {
        cursor++;
    }

    /**
     * Checks if the character is allowed in a number.
     *
     * @param c the character to check
     * @return true if the character is allowed to appear in a number
     */
    public static boolean isAllowedNumber(final char c) {
        return c >= '0' && c <= '9' || c == '.' || c == '-';
    }

    /**
     * Skips all following whitespace characters as determined by {@link Character#isWhitespace(char)}.
     */
    public void skipWhitespace() {
        while (canRead() && Character.isWhitespace(peek())) {
            skip();
        }
    }

    /**
     * Reads an integer.
     * <p>
     * The integer may only contain characters that match {@link #isAllowedNumber(char)}.
     *
     * @return the read integer
     * @throws CommandSyntaxException if the command is no proper int
     */
    public int readInt() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext(this);
        }
        try {
            return Integer.parseInt(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(this, number);
        }
    }

    /**
     * Reads a long.
     * <p>
     * The long may only contain characters that match {@link #isAllowedNumber(char)}.
     *
     * @return the read long
     * @throws CommandSyntaxException if the command is no proper long
     */
    public long readLong() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext(this);
        }
        try {
            return Long.parseLong(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext(this, number);
        }
    }

    /**
     * Reads a double.
     * <p>
     * The double may only contain characters that match {@link #isAllowedNumber(char)}.
     *
     * @return the read double
     * @throws CommandSyntaxException if the command is no proper double
     */
    public double readDouble() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(this);
        }
        try {
            return Double.parseDouble(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(this, number);
        }
    }

    /**
     * Reads a float.
     * <p>
     * The float may only contain characters that match {@link #isAllowedNumber(char)}.
     *
     * @return the read float
     * @throws CommandSyntaxException if the command is no proper float
     */
    public float readFloat() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }
        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext(this);
        }
        try {
            return Float.parseFloat(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext(this, number);
        }
    }

    /**
     * Checks if a character is allowed in an unquoted string or needs to be escaped.
     *
     * <em>Currently</em> allowed characters are:<br>
     * {@code [0-9A-Za-z_\-.+]} (as a regular expression set)
     *
     * @param c the character to check
     * @return true if the character is allowed to appear in an unquoted string
     */
    public static boolean isAllowedInUnquotedString(final char c) {
        return c >= '0' && c <= '9'
            || c >= 'A' && c <= 'Z'
            || c >= 'a' && c <= 'z'
            || c == '_' || c == '-'
            || c == '.' || c == '+';
    }

    /**
     * Reads an unquoted string, {@literal i.e.} for as long as {@link #isAllowedInUnquotedString(char)} returns true.
     *
     * @return the read string
     */
    public String readUnquotedString() {
        final int start = cursor;
        while (canRead() && isAllowedInUnquotedString(peek())) {
            skip();
        }
        return string.substring(start, cursor);
    }

    // @formatter:off
    /**
     * Returns a quoted string.
     *
     * The format of a quoted string is as follows:
     * <ul>
     *     <li>
     *         Starts and ends with {@code "} (quotation mark)
     *     </li>
     *     <li>
     *         Escape character {@code \} (backslash) has to be used for quotation marks and literal escape
     *         characters within the string
     *     </li>
     * </ul>
     * @return a quoted string or an empty string, if {@link #canRead} is false
     * @throws CommandSyntaxException if the next character is not a quote, an invalid escape character is encountered
     * or the closing quote was not found
     */
    // @formatter:on
    public String readQuotedString() throws CommandSyntaxException {
        if (!canRead()) {
            return "";
        } else if (peek() != SYNTAX_QUOTE) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext(this);
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
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape().createWithContext(this, String.valueOf(c));
                }
            } else if (c == SYNTAX_ESCAPE) {
                escaped = true;
            } else if (c == SYNTAX_QUOTE) {
                return result.toString();
            } else {
                result.append(c);
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext(this);
    }

    /**
     * Reads a string, deciding between {@link #readQuotedString} and {@link #readUnquotedString} based on whether the
     * next character is a quote.
     *
     * @return the read string
     * @throws CommandSyntaxException if an error occurs parsing the string
     */
    public String readString() throws CommandSyntaxException {
        if (canRead() && peek() == SYNTAX_QUOTE) {
            return readQuotedString();
        } else {
            return readUnquotedString();
        }
    }

    /**
     * Reads a single boolean.
     * <p>
     * A boolean can be either {@code true} or {@code false} and is case sensitive.
     *
     * @return the read boolean
     * @throws CommandSyntaxException if the input was empty or the read string is not a valid boolean
     */
    public boolean readBoolean() throws CommandSyntaxException {
        final int start = cursor;
        final String value = readString();
        if (value.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext(this);
        }

        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().createWithContext(this, value);
        }
    }

    /**
     * Peeks at the next char and consumes it, if it is the expected character{@literal .} If not, it throws an
     * exception.
     *
     * @param c the character that is expected to occur next
     * @throws CommandSyntaxException if the character was not the expected character or the end of the input was
     * reached
     */
    public void expect(final char c) throws CommandSyntaxException {
        if (!canRead() || peek() != c) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(this, String.valueOf(c));
        }
        skip();
    }
}
