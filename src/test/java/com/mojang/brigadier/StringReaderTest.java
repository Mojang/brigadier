package com.mojang.brigadier;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
    public void readUnquotedString() throws Exception {
        final StringReader reader = new StringReader("hello world");
        assertThat(reader.readUnquotedString(), equalTo("hello"));
        assertThat(reader.getRead(), equalTo("hello"));
        assertThat(reader.getRemaining(), equalTo(" world"));
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
    public void readQuotedString() throws Exception {
        final StringReader reader = new StringReader("\"hello world\"");
        assertThat(reader.readQuotedString(), equalTo("hello world"));
        assertThat(reader.getRead(), equalTo("\"hello world\""));
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
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_EXPECTED_START_OF_QUOTE));
            assertThat(ex.getData(), equalTo(Collections.emptyMap()));
        }
    }

    @Test
    public void readQuotedString_noClose() throws Exception {
        try {
            new StringReader("\"hello world").readQuotedString();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_EXPECTED_END_OF_QUOTE));
            assertThat(ex.getData(), equalTo(Collections.emptyMap()));
        }
    }

    @Test
    public void readQuotedString_invalidEscape() throws Exception {
        try {
            new StringReader("\"hello\\nworld\"").readQuotedString();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_INVALID_ESCAPE));
            assertThat(ex.getData(), equalTo(ImmutableMap.of("character", "n")));
        }
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
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_INVALID_INT));
            assertThat(ex.getData(), equalTo(ImmutableMap.of("value", "12.34")));
        }
    }

    @Test
    public void readInt_none() throws Exception {
        try {
            new StringReader("").readInt();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_EXPECTED_INT));
            assertThat(ex.getData(), equalTo(Collections.emptyMap()));
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
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_INVALID_DOUBLE));
            assertThat(ex.getData(), equalTo(ImmutableMap.of("value", "12.34.56")));
        }
    }

    @Test
    public void readDouble_none() throws Exception {
        try {
            new StringReader("").readDouble();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_EXPECTED_DOUBLE));
            assertThat(ex.getData(), equalTo(Collections.emptyMap()));
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
}