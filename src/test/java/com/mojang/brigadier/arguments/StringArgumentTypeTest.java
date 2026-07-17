// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.StringArgumentType.escapeIfRequired;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringArgumentTypeTest {
    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void testParseWord_greedyCharset() throws Exception {
        final StringReader reader = mock(StringReader.class);
        when(reader.readUnquotedStringGreedy()).thenReturn("1+1=2");
        assertThat(word(true).parse(reader), equalTo("1+1=2"));
        verify(reader).readUnquotedStringGreedy();
    }

    @Test
    public void testParseString_greedyCharset() throws Exception {
        final StringReader reader = mock(StringReader.class);
        when(reader.readStringGreedy()).thenReturn("1+1=2 2+2=4");
        assertThat(string(true).parse(reader), equalTo("1+1=2 2+2=4"));
        verify(reader).readStringGreedy();
    }

    @Test
    public void testParseWord() throws Exception {
        final StringReader reader = mock(StringReader.class);
        when(reader.readUnquotedString()).thenReturn("hello");
        assertThat(word().parse(reader), equalTo("hello"));
        verify(reader).readUnquotedString();
    }

    @Test
    public void testParseString() throws Exception {
        final StringReader reader = mock(StringReader.class);
        when(reader.readString()).thenReturn("hello world");
        assertThat(string().parse(reader), equalTo("hello world"));
        verify(reader).readString();
    }

    @Test
    public void testParseGreedyString() throws Exception {
        final StringReader reader = new StringReader("Hello world! This is a test.");
        assertThat(greedyString().parse(reader), equalTo("Hello world! This is a test."));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(string(), hasToString("string()"));
    }

    @Test
    public void testEscapeIfRequired_notRequired() throws Exception {
        assertThat(escapeIfRequired("hello"), is(equalTo("hello")));
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