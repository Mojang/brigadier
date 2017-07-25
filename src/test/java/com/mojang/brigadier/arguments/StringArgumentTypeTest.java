package com.mojang.brigadier.arguments;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.mojang.brigadier.arguments.StringArgumentType.escapeIfRequired;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseWord_empty() throws Exception {
        type = word();
        ParsedArgument<Object, String> result = type.parse("", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is(""));
        assertThat(result.getResult(), is(""));
    }

    @Test
    public void testParseWord_simple() throws Exception {
        type = word();
        ParsedArgument<Object, String> result = type.parse("hello", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseString() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseGreedyString() throws Exception {
        type = greedyString();
        ParsedArgument<Object, String> result = type.parse("hello world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello world"));
        assertThat(result.getResult(), is("hello world"));
    }

    @Test
    public void testParse() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseWordQuoted() throws Exception {
        type = word();
        ParsedArgument<Object, String> result = type.parse("\"hello \\\" world\"", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is(""));
        assertThat(result.getResult(), is(""));
    }

    @Test
    public void testParseQuoted() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("\"hello \\\" world\"", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("\"hello \\\" world\""));
        assertThat(result.getResult(), is("hello \" world"));
    }

    @Test
    public void testParseQuotedWithRemaining() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("\"hello \\\" world\" with remaining", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("\"hello \\\" world\""));
        assertThat(result.getResult(), is("hello \" world"));
    }

    @Test
    public void testParseNotQuoted() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseQuote_earlyUnquoteWithRemaining() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("\"hello\" world", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("\"hello\""));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseQuote_lateQuoteWithRemaining() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("hello \"world\"", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("hello"));
        assertThat(result.getResult(), is("hello"));
    }

    @Test
    public void testParseEmpty() throws Exception {
        type = string();
        ParsedArgument<Object, String> result = type.parse("", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is(""));
        assertThat(result.getResult(), is(""));
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