// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class StringReaderTest {
    @Test
    public void canRead() throws Exception {
        final StringReader reader = new StringReader("abc");
        assertThat(reader.canRead(), is(true));
        reader.skip(); // 'a'
        assertThat(reader.canRead(), is(true));
        reader.skip(); // 'b'
        assertThat(reader.canRead(), is(true));
        reader.skip(); // 'c'
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void getRemainingLength() throws Exception {
        final StringReader reader = new StringReader("abc");
        assertThat(reader.getRemainingLength(), is(3));
        reader.setCursor(1);
        assertThat(reader.getRemainingLength(), is(2));
        reader.setCursor(2);
        assertThat(reader.getRemainingLength(), is(1));
        reader.setCursor(3);
        assertThat(reader.getRemainingLength(), is(0));
    }

    @Test
    public void canRead_length() throws Exception {
        final StringReader reader = new StringReader("abc");
        assertThat(reader.canRead(1), is(true));
        assertThat(reader.canRead(2), is(true));
        assertThat(reader.canRead(3), is(true));
        assertThat(reader.canRead(4), is(false));
        assertThat(reader.canRead(5), is(false));
    }

    @Test
    public void peek() throws Exception {
        final StringReader reader = new StringReader("abc");
        assertThat(reader.peek(), is('a'));
        assertThat(reader.getCursor(), is(0));
        reader.setCursor(2);
        assertThat(reader.peek(), is('c'));
        assertThat(reader.getCursor(), is(2));
    }

    @Test
    public void peek_length() throws Exception {
        final StringReader reader = new StringReader("abc");
        assertThat(reader.peek(0), is('a'));
        assertThat(reader.peek(2), is('c'));
        assertThat(reader.getCursor(), is(0));
        reader.setCursor(1);
        assertThat(reader.peek(1), is('c'));
        assertThat(reader.getCursor(), is(1));
    }

    @Test
    public void read() throws Exception {
        final StringReader reader = new StringReader("abc");
        assertThat(reader.read(), is('a'));
        assertThat(reader.read(), is('b'));
        assertThat(reader.read(), is('c'));
        assertThat(reader.getCursor(), is(3));
    }

    @Test
    public void skip() throws Exception {
        final StringReader reader = new StringReader("abc");
        reader.skip();
        assertThat(reader.getCursor(), is(1));
    }

    @Test
    public void getRemaining() throws Exception {
        final StringReader reader = new StringReader("Hello!");
        assertThat(reader.getRemaining(), equalTo("Hello!"));
        reader.setCursor(3);
        assertThat(reader.getRemaining(), equalTo("lo!"));
        reader.setCursor(6);
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void getRead() throws Exception {
        final StringReader reader = new StringReader("Hello!");
        assertThat(reader.getRead(), equalTo(""));
        reader.setCursor(3);
        assertThat(reader.getRead(), equalTo("Hel"));
        reader.setCursor(6);
        assertThat(reader.getRead(), equalTo("Hello!"));
    }

    @Test
    public void skipWhitespace_none() throws Exception {
        final StringReader reader = new StringReader("Hello!");
        reader.skipWhitespace();
        assertThat(reader.getCursor(), is(0));
    }

    @Test
    public void skipWhitespace_mixed() throws Exception {
        final StringReader reader = new StringReader(" \t \t\nHello!");
        reader.skipWhitespace();
        assertThat(reader.getCursor(), is(5));
    }

    @Test
    public void skipWhitespace_empty() throws Exception {
        final StringReader reader = new StringReader("");
        reader.skipWhitespace();
        assertThat(reader.getCursor(), is(0));
    }

    @Test
    public void readUnquotedString() throws Exception {
        final StringReader reader = new StringReader("hello world");
        assertThat(reader.readUnquotedString(), equalTo("hello"));
        assertThat(reader.getRead(), equalTo("hello"));
        assertThat(reader.getRemaining(), equalTo(" world"));
    }

    @Test
    public void readUnquotedString_strictCharset() throws Exception {
        final StringReader reader = new StringReader("1+1=2 2+2=4");
        assertThat(reader.readString(), equalTo("1+1"));
        assertThat(reader.getRead(), equalTo("1+1"));
        assertThat(reader.getRemaining(), equalTo("=2 2+2=4"));

        // Should not be able to read further -- as invalid character is present
        assertThat(reader.readString(), equalTo(""));
    }

    @Test
    public void readUnquotedString_strictCharsetQuoted() throws Exception {
        final StringReader reader = new StringReader("\"1+1=2\" \"2+2=4\"");
        assertThat(reader.readString(), equalTo("1+1=2"));
        assertThat(reader.getRead(), equalTo("\"1+1=2\""));
        assertThat(reader.getRemaining(), equalTo(" \"2+2=4\""));

        reader.skipWhitespace();

        assertThat(reader.readString(), equalTo("2+2=4"));
        assertThat(reader.getRead(), equalTo("\"1+1=2\" \"2+2=4\""));
    }

    @Test
    public void readUnquotedString_empty() throws Exception {
        final StringReader reader = new StringReader("");
        assertThat(reader.readUnquotedString(), equalTo(""));
        assertThat(reader.getRead(), equalTo(""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readUnquotedString_empty_withRemaining() throws Exception {
        final StringReader reader = new StringReader(" hello world");
        assertThat(reader.readUnquotedString(), equalTo(""));
        assertThat(reader.getRead(), equalTo(""));
        assertThat(reader.getRemaining(), equalTo(" hello world"));
    }

    @Test
    public void readUnquotedStringGreedy() throws Exception {
        final StringReader reader = new StringReader("hello world");
        assertThat(reader.readStringGreedy(), equalTo("hello"));
        assertThat(reader.getRead(), equalTo("hello"));
        assertThat(reader.getRemaining(), equalTo(" world"));
    }

    @Test
    public void readUnquotedStringGreedy_strictCharset() throws Exception {
        final StringReader reader = new StringReader("1+1=2 2+2=4");
        assertThat(reader.readStringGreedy(), equalTo("1+1=2"));
        assertThat(reader.getRead(), equalTo("1+1=2"));
        assertThat(reader.getRemaining(), equalTo(" 2+2=4"));
        reader.skipWhitespace();
        assertThat(reader.readStringGreedy(), equalTo("2+2=4"));
    }

    @Test
    public void readUnquotedStringGreedy_empty() throws Exception {
        final StringReader reader = new StringReader("");
        assertThat(reader.readUnquotedStringGreedy(), equalTo(""));
        assertThat(reader.getRead(), equalTo(""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readUnquotedStringGreedy_empty_withRemaining() throws Exception {
        final StringReader reader = new StringReader(" hello world");
        assertThat(reader.readUnquotedStringGreedy(), equalTo(""));
        assertThat(reader.getRead(), equalTo(""));
        assertThat(reader.getRemaining(), equalTo(" hello world"));
    }

    @Test
    public void readQuotedString() throws Exception {
        final StringReader reader = new StringReader("\"hello world\"");
        assertThat(reader.readQuotedString(), equalTo("hello world"));
        assertThat(reader.getRead(), equalTo("\"hello world\""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readSingleQuotedString() throws Exception {
        final StringReader reader = new StringReader("'hello world'");
        assertThat(reader.readQuotedString(), equalTo("hello world"));
        assertThat(reader.getRead(), equalTo("'hello world'"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readMixedQuotedString_doubleInsideSingle() throws Exception {
        final StringReader reader = new StringReader("'hello \"world\"'");
        assertThat(reader.readQuotedString(), equalTo("hello \"world\""));
        assertThat(reader.getRead(), equalTo("'hello \"world\"'"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readMixedQuotedString_singleInsideDouble() throws Exception {
        final StringReader reader = new StringReader("\"hello 'world'\"");
        assertThat(reader.readQuotedString(), equalTo("hello 'world'"));
        assertThat(reader.getRead(), equalTo("\"hello 'world'\""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readQuotedString_empty() throws Exception {
        final StringReader reader = new StringReader("");
        assertThat(reader.readQuotedString(), equalTo(""));
        assertThat(reader.getRead(), equalTo(""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readQuotedString_emptyQuoted() throws Exception {
        final StringReader reader = new StringReader("\"\"");
        assertThat(reader.readQuotedString(), equalTo(""));
        assertThat(reader.getRead(), equalTo("\"\""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readQuotedString_emptyQuoted_withRemaining() throws Exception {
        final StringReader reader = new StringReader("\"\" hello world");
        assertThat(reader.readQuotedString(), equalTo(""));
        assertThat(reader.getRead(), equalTo("\"\""));
        assertThat(reader.getRemaining(), equalTo(" hello world"));
    }

    @Test
    public void readQuotedString_withEscapedQuote() throws Exception {
        final StringReader reader = new StringReader("\"hello \\\"world\\\"\"");
        assertThat(reader.readQuotedString(), equalTo("hello \"world\""));
        assertThat(reader.getRead(), equalTo("\"hello \\\"world\\\"\""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readQuotedString_withEscapedEscapes() throws Exception {
        final StringReader reader = new StringReader("\"\\\\o/\"");
        assertThat(reader.readQuotedString(), equalTo("\\o/"));
        assertThat(reader.getRead(), equalTo("\"\\\\o/\""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readQuotedString_withRemaining() throws Exception {
        final StringReader reader = new StringReader("\"hello world\" foo bar");
        assertThat(reader.readQuotedString(), equalTo("hello world"));
        assertThat(reader.getRead(), equalTo("\"hello world\""));
        assertThat(reader.getRemaining(), equalTo(" foo bar"));
    }

    @Test
    public void readQuotedString_withImmediateRemaining() throws Exception {
        final StringReader reader = new StringReader("\"hello world\"foo bar");
        assertThat(reader.readQuotedString(), equalTo("hello world"));
        assertThat(reader.getRead(), equalTo("\"hello world\""));
        assertThat(reader.getRemaining(), equalTo("foo bar"));
    }

    @Test
    public void readQuotedString_noOpen() throws Exception {
        try {
            new StringReader("hello world\"").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readQuotedString_noClose() throws Exception {
        try {
            new StringReader("\"hello world").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote()));
            assertThat(ex.getCursor(), is(12));
        }
    }

    @Test
    public void readQuotedString_invalidEscape() throws Exception {
        try {
            new StringReader("\"hello\\nworld\"").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape()));
            assertThat(ex.getCursor(), is(7));
        }
    }

    @Test
    public void readQuotedString_invalidQuoteEscape() throws Exception {
        try {
            new StringReader("'hello\\\"\'world").readQuotedString();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape()));
            assertThat(ex.getCursor(), is(7));
        }
    }

    @Test
    public void readString_noQuotes() throws Exception {
        final StringReader reader = new StringReader("hello world");
        assertThat(reader.readString(), equalTo("hello"));
        assertThat(reader.getRead(), equalTo("hello"));
        assertThat(reader.getRemaining(), equalTo(" world"));
    }

    @Test
    public void readString_singleQuotes() throws Exception {
        final StringReader reader = new StringReader("'hello world'");
        assertThat(reader.readString(), equalTo("hello world"));
        assertThat(reader.getRead(), equalTo("'hello world'"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readString_doubleQuotes() throws Exception {
        final StringReader reader = new StringReader("\"hello world\"");
        assertThat(reader.readString(), equalTo("hello world"));
        assertThat(reader.getRead(), equalTo("\"hello world\""));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readInt() throws Exception {
        final StringReader reader = new StringReader("1234567890");
        assertThat(reader.readInt(), is(1234567890));
        assertThat(reader.getRead(), equalTo("1234567890"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readInt_negative() throws Exception {
        final StringReader reader = new StringReader("-1234567890");
        assertThat(reader.readInt(), is(-1234567890));
        assertThat(reader.getRead(), equalTo("-1234567890"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readInt_invalid() throws Exception {
        try {
            new StringReader("12.34").readInt();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readInt_none() throws Exception {
        try {
            new StringReader("").readInt();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readInt_withRemaining() throws Exception {
        final StringReader reader = new StringReader("1234567890 foo bar");
        assertThat(reader.readInt(), is(1234567890));
        assertThat(reader.getRead(), equalTo("1234567890"));
        assertThat(reader.getRemaining(), equalTo(" foo bar"));
    }

    @Test
    public void readInt_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("1234567890foo bar");
        assertThat(reader.readInt(), is(1234567890));
        assertThat(reader.getRead(), equalTo("1234567890"));
        assertThat(reader.getRemaining(), equalTo("foo bar"));
    }

    @Test
    public void readLong() throws Exception {
        final StringReader reader = new StringReader("1234567890");
        assertThat(reader.readLong(), is(1234567890L));
        assertThat(reader.getRead(), equalTo("1234567890"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readLong_negative() throws Exception {
        final StringReader reader = new StringReader("-1234567890");
        assertThat(reader.readLong(), is(-1234567890L));
        assertThat(reader.getRead(), equalTo("-1234567890"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readLong_invalid() throws Exception {
        try {
            new StringReader("12.34").readLong();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readLong_none() throws Exception {
        try {
            new StringReader("").readLong();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readLong_withRemaining() throws Exception {
        final StringReader reader = new StringReader("1234567890 foo bar");
        assertThat(reader.readLong(), is(1234567890L));
        assertThat(reader.getRead(), equalTo("1234567890"));
        assertThat(reader.getRemaining(), equalTo(" foo bar"));
    }

    @Test
    public void readLong_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("1234567890foo bar");
        assertThat(reader.readLong(), is(1234567890L));
        assertThat(reader.getRead(), equalTo("1234567890"));
        assertThat(reader.getRemaining(), equalTo("foo bar"));
    }

    @Test
    public void readDouble() throws Exception {
        final StringReader reader = new StringReader("123");
        assertThat(reader.readDouble(), is(123.0));
        assertThat(reader.getRead(), equalTo("123"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readDouble_withDecimal() throws Exception {
        final StringReader reader = new StringReader("12.34");
        assertThat(reader.readDouble(), is(12.34));
        assertThat(reader.getRead(), equalTo("12.34"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readDouble_negative() throws Exception {
        final StringReader reader = new StringReader("-123");
        assertThat(reader.readDouble(), is(-123.0));
        assertThat(reader.getRead(), equalTo("-123"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readDouble_invalid() throws Exception {
        try {
            new StringReader("12.34.56").readDouble();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readDouble_none() throws Exception {
        try {
            new StringReader("").readDouble();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readDouble_withRemaining() throws Exception {
        final StringReader reader = new StringReader("12.34 foo bar");
        assertThat(reader.readDouble(), is(12.34));
        assertThat(reader.getRead(), equalTo("12.34"));
        assertThat(reader.getRemaining(), equalTo(" foo bar"));
    }

    @Test
    public void readDouble_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("12.34foo bar");
        assertThat(reader.readDouble(), is(12.34));
        assertThat(reader.getRead(), equalTo("12.34"));
        assertThat(reader.getRemaining(), equalTo("foo bar"));
    }

    @Test
    public void readFloat() throws Exception {
        final StringReader reader = new StringReader("123");
        assertThat(reader.readFloat(), is(123.0f));
        assertThat(reader.getRead(), equalTo("123"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readFloat_withDecimal() throws Exception {
        final StringReader reader = new StringReader("12.34");
        assertThat(reader.readFloat(), is(12.34f));
        assertThat(reader.getRead(), equalTo("12.34"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readFloat_negative() throws Exception {
        final StringReader reader = new StringReader("-123");
        assertThat(reader.readFloat(), is(-123.0f));
        assertThat(reader.getRead(), equalTo("-123"));
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void readFloat_invalid() throws Exception {
        try {
            new StringReader("12.34.56").readFloat();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readFloat_none() throws Exception {
        try {
            new StringReader("").readFloat();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readFloat_withRemaining() throws Exception {
        final StringReader reader = new StringReader("12.34 foo bar");
        assertThat(reader.readFloat(), is(12.34f));
        assertThat(reader.getRead(), equalTo("12.34"));
        assertThat(reader.getRemaining(), equalTo(" foo bar"));
    }

    @Test
    public void readFloat_withRemainingImmediate() throws Exception {
        final StringReader reader = new StringReader("12.34foo bar");
        assertThat(reader.readFloat(), is(12.34f));
        assertThat(reader.getRead(), equalTo("12.34"));
        assertThat(reader.getRemaining(), equalTo("foo bar"));
    }

    @Test
    public void expect_correct() throws Exception {
        final StringReader reader = new StringReader("abc");
        reader.expect('a');
        assertThat(reader.getCursor(), is(1));
    }

    @Test
    public void expect_incorrect() throws Exception {
        final StringReader reader = new StringReader("bcd");
        try {
            reader.expect('a');
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void expect_none() throws Exception {
        final StringReader reader = new StringReader("");
        try {
            reader.expect('a');
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readBoolean_correct() throws Exception {
        final StringReader reader = new StringReader("true");
        assertThat(reader.readBoolean(), is(true));
        assertThat(reader.getRead(), equalTo("true"));
    }

    @Test
    public void readBoolean_incorrect() throws Exception {
        final StringReader reader = new StringReader("tuesday");
        try {
            reader.readBoolean();
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void readBoolean_none() throws Exception {
        final StringReader reader = new StringReader("");
        try {
            reader.readBoolean();
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool()));
            assertThat(ex.getCursor(), is(0));
        }
    }
}