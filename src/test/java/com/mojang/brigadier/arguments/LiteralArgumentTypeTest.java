// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.LiteralArgumentType.literal;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LiteralArgumentTypeTest {
    private LiteralArgumentType type;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        this.type = literal();
    }

    @Test
    public void testParseSingleWord() throws Exception {
        StringReader reader = new StringReader("test");
        assertThat(this.type.parse(reader), equalTo("test"));
        // cursor should be at end
        assertThat(reader.getCursor(), equalTo("test".length()));
    }

    @Test
    public void testParseMultipleWords() throws Exception {
        StringReader reader = new StringReader("foo bar baz");
        assertThat(this.type.parse(reader), equalTo("foo"));
        reader.skipWhitespace();
        assertThat(this.type.parse(reader), equalTo("bar"));
        reader.skipWhitespace();
        assertThat(this.type.parse(reader), equalTo("baz"));
    }

    @Test
    public void testParseEmptyInput() throws Exception {
        StringReader reader = new StringReader("");
        assertThat(this.type.parse(reader), equalTo(""));
        assertThat(reader.getCursor(), equalTo(0));
    }

    @Test
    public void testParseWithLeadingSpaces() throws Exception {
        StringReader reader = new StringReader("   spaced out");
        // leading spaces -> empty literal
        assertThat(this.type.parse(reader), equalTo(""));
        reader.skipWhitespace();
        assertThat(this.type.parse(reader), equalTo("spaced"));
        reader.skipWhitespace();
        assertThat(this.type.parse(reader), equalTo("out"));
    }

    @Test
    public void testParsePunctuation() throws Exception {
        StringReader reader = new StringReader("hello,world! next");
        assertThat(this.type.parse(reader), equalTo("hello,world!"));
        reader.skipWhitespace();
        assertThat(this.type.parse(reader), equalTo("next"));
    }

    @Test
    public void testParseAdvancedCharset() throws Exception {
        StringReader reader = new StringReader("Ich bin glücklich");
        assertThat(this.type.parse(reader), equalTo("Ich"));
        reader.skipWhitespace();
        assertThat(this.type.parse(reader), equalTo("bin"));
        reader.skipWhitespace();
        assertThat(this.type.parse(reader), equalTo("glücklich"));
    }

    @Test
    public void testToString() {
        assertThat(this.type.toString(), equalTo("literal_arg()"));
    }

}