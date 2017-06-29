package com.mojang.brigadier.arguments;

import com.google.common.collect.Sets;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import com.sun.xml.internal.ws.api.ComponentEx;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static com.mojang.brigadier.arguments.StringArgumentType.ERROR_EXPECTED_END_OF_QUOTE;
import static com.mojang.brigadier.arguments.StringArgumentType.ERROR_INVALID_ESCAPE;
import static com.mojang.brigadier.arguments.StringArgumentType.ERROR_UNEXPECTED_END_OF_QUOTE;
import static com.mojang.brigadier.arguments.StringArgumentType.ERROR_UNEXPECTED_ESCAPE;
import static com.mojang.brigadier.arguments.StringArgumentType.ERROR_UNEXPECTED_START_OF_QUOTE;
import static com.mojang.brigadier.arguments.StringArgumentType.escapeIfRequired;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class StringArgumentTypeTest {
    private StringArgumentType type;

    @Test
    public void testParseWord() throws Exception {
        type = word();
        ParsedArgument<String> result = type.parse("hello world");

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseWord_empty() throws Exception {
        type = word();
        ParsedArgument<String> result = type.parse("");

        assertThat(result.getRaw(), is(""));
        assertThat(result.getResult(), is(""));
    }

    @Test
    public void testParseWord_simple() throws Exception {
        type = word();
        ParsedArgument<String> result = type.parse("hello");

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseString() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("hello world");

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseGreedyString() throws Exception {
        type = greedyString();
        ParsedArgument<String> result = type.parse("hello world");

        assertThat(result.getRaw(), is("hello world"));
        assertThat(result.getResult(), is("hello world"));
    }

    @Test
    public void testParse() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("hello");

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseWordQuoted() throws Exception {
        type = word();
        ParsedArgument<String> result = type.parse("\"hello \\\" world\"");

        assertThat(result.getRaw(), is("\"hello"));
        assertThat(result.getResult(), is("\"hello"));
    }

    @Test
    public void testParseQuoted() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("\"hello \\\" world\"");

        assertThat(result.getRaw(), is("\"hello \\\" world\""));
        assertThat(result.getResult(), is("hello \" world"));
    }

    @Test
    public void testParseQuotedWithRemaining() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("\"hello \\\" world\" with remaining");

        assertThat(result.getRaw(), is("\"hello \\\" world\""));
        assertThat(result.getResult(), is("hello \" world"));
    }

    @Test
    public void testParseNotQuoted() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("hello world");

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseInvalidQuote_earlyUnquote() throws Exception {
        try {
            type = string();
            type.parse("\"hello \"world");
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_UNEXPECTED_END_OF_QUOTE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseQuote_earlyUnquoteWithRemaining() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("\"hello\" world");

        assertThat(result.getRaw(), is("\"hello\""));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseInvalidQuote_lateQuote() throws Exception {
        try {
            type = string();
            type.parse("hello\" world\"");
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_UNEXPECTED_START_OF_QUOTE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseQuote_lateQuoteWithRemaining() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("hello \"world\"");

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseInvalidQuote_middleQuote() throws Exception {
        try {
            type = string();
            type.parse("hel\"lo");
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_UNEXPECTED_START_OF_QUOTE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseInvalidQuote_noUnquote() throws Exception {
        try {
            type = string();
            type.parse("\"hello world");
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_EXPECTED_END_OF_QUOTE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseEmpty() throws Exception {
        type = string();
        ParsedArgument<String> result = type.parse("");

        assertThat(result.getRaw(), is(""));
        assertThat(result.getResult(), is(""));
    }

    @Test
    public void testParseInvalidEscape_onlyEscape() throws Exception {
        try {
            type = string();
            type.parse("\\");
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_UNEXPECTED_ESCAPE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseInvalidEscape_unknownSequence() throws Exception {
        try {
            type = string();
            type.parse("\"\\n\"");
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_INVALID_ESCAPE));
            assertThat(e.getData(), is(equalTo(Collections.singletonMap("input", "\\n"))));
        }
    }

    @Test
    public void testParseInvalidEscape_notQuoted() throws Exception {
        try {
            type = string();
            type.parse("hel\\\\o");
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_UNEXPECTED_ESCAPE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testSuggestions() throws Exception {
        type = string();
        Set<String> set = Sets.newHashSet();
        type.listSuggestions("", set);
        assertThat(set, is(empty()));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(string(), hasToString("string()"));
    }

    @Test
    public void testEscapeIfRequired_notRequired() throws Exception {
        assertThat(escapeIfRequired("hello!"), is(equalTo("hello!")));
        assertThat(escapeIfRequired(""), is(equalTo("")));
    }

    @Test
    public void testEscapeIfRequired_multipleWords() throws Exception {
        assertThat(escapeIfRequired("hello world"), is(equalTo("\"hello world\"")));
    }

    @Test
    public void testEscapeIfRequired_quote() throws Exception {
        assertThat(escapeIfRequired("hello \"world\"!"), is(equalTo("\"hello \\\"world\\\"!\"")));
    }

    @Test
    public void testEscapeIfRequired_escapes() throws Exception {
        assertThat(escapeIfRequired("\\"), is(equalTo("\"\\\\\"")));
    }

    @Test
    public void testEscapeIfRequired_singleQuote() throws Exception {
        assertThat(escapeIfRequired("\""), is(equalTo("\"\\\"\"")));
    }
}