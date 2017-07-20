package com.mojang.brigadier.arguments;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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

@RunWith(MockitoJUnitRunner.class)
public class StringArgumentTypeTest {
    private StringArgumentType type;
    @Mock
    private Object source;
    @Mock
    private CommandDispatcher<Object> dispatcher;

    @Test
    public void testParseWord() throws Exception {
        type = word();
        ParsedArgument<Object, String> result = type.parse("hello world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(source), is("hello"));
    }

    @Test
    public void testParseWord_empty() throws Exception {
        type = word();
        ParsedArgument<Object, String> result = type.parse("", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is(""));
        assertThat(result.getResult(source), is(""));
    }

    @Test
    public void testParseWord_simple() throws Exception {
        type = word();
        ParsedArgument<Object, String> result = type.parse("hello", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(source), is("hello"));
    }

    @Test
    public void testParseString() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(source), is("hello"));
    }

    @Test
    public void testParseGreedyString() throws Exception {
        type = greedyString();
        ParsedArgument<Object, String> result = type.parse("hello world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello world"));
        assertThat(result.getResult(source), is("hello world"));
    }

    @Test
    public void testParse() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(source), is("hello"));
    }

    @Test
    public void testParseWordQuoted() throws Exception {
        type = word();
        ParsedArgument<Object, String> result = type.parse("\"hello \\\" world\"", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("\"hello"));
        assertThat(result.getResult(source), is("\"hello"));
    }

    @Test
    public void testParseQuoted() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("\"hello \\\" world\"", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("\"hello \\\" world\""));
        assertThat(result.getResult(source), is("hello \" world"));
    }

    @Test
    public void testParseQuotedWithRemaining() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("\"hello \\\" world\" with remaining", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("\"hello \\\" world\""));
        assertThat(result.getResult(source), is("hello \" world"));
    }

    @Test
    public void testParseNotQuoted() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(source), is("hello"));
    }

    @Test
    public void testParseInvalidQuote_earlyUnquote() throws Exception {
        try {
            type = string();
            type.parse("\"hello \"world", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_UNEXPECTED_END_OF_QUOTE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseQuote_earlyUnquoteWithRemaining() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("\"hello\" world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("\"hello\""));
        assertThat(result.getResult(source), is("hello"));
    }

    @Test
    public void testParseInvalidQuote_lateQuote() throws Exception {
        try {
            type = string();
            type.parse("hello\" world\"", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_UNEXPECTED_START_OF_QUOTE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseQuote_lateQuoteWithRemaining() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello \"world\"", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(source), is("hello"));
    }

    @Test
    public void testParseInvalidQuote_middleQuote() throws Exception {
        try {
            type = string();
            type.parse("hel\"lo", new CommandContextBuilder<>(dispatcher, source));
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
            type.parse("\"hello world", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException e) {
            assertThat(e.getType(), is(ERROR_EXPECTED_END_OF_QUOTE));
            assertThat(e.getData(), is(equalTo(Collections.emptyMap())));
        }
    }

    @Test
    public void testParseEmpty() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is(""));
        assertThat(result.getResult(source), is(""));
    }

    @Test
    public void testParseInvalidEscape_onlyEscape() throws Exception {
        try {
            type = string();
            type.parse("\\", new CommandContextBuilder<>(dispatcher, source));
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
            type.parse("\"\\n\"", new CommandContextBuilder<>(dispatcher, source));
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
            type.parse("hel\\\\o", new CommandContextBuilder<>(dispatcher, source));
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
        @SuppressWarnings("unchecked") final CommandContextBuilder<Object> context = Mockito.mock(CommandContextBuilder.class);
        type.listSuggestions("", set, context);
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