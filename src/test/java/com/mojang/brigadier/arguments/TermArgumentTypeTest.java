// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.TermArgumentType.term;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermArgumentTypeTest {
    @Mock
    private CommandContextBuilder<Object> context;

    @Test
    public void testParseTerm() throws Exception {
        final StringReader reader = mock(StringReader.class);
        when(reader.readUnquotedString()).thenReturn("hello");
        assertThat(term("hello","world").parse(reader), equalTo("hello"));
        verify(reader).readUnquotedString();
    }

    @Test
    public void testParseTermFails() {
        final StringReader reader = mock(StringReader.class);
        when(reader.readUnquotedString()).thenReturn("foo");
        try {
            term("hello","world").parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.termInvalid()));
        }
    }
}