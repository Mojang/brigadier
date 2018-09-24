// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SimpleCommandSyntaxExceptionTypeTest {
    @Test
    public void createWithContext() throws Exception {
        final SimpleCommandExceptionType type = new SimpleCommandExceptionType(new LiteralMessage("error"));
        final StringReader reader = new StringReader("Foo bar");
        reader.setCursor(5);
        final CommandSyntaxException exception = type.createWithContext(reader);
        assertThat(exception.getType(), is(type));
        assertThat(exception.getInput(), is("Foo bar"));
        assertThat(exception.getCursor(), is(5));
    }


    @Test
    public void getContext_none() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(mock(CommandExceptionType.class), new LiteralMessage("error"));
        assertThat(exception.getContext(), is(nullValue()));
    }

    @Test
    public void getContext_short() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(mock(CommandExceptionType.class), new LiteralMessage("error"), "Hello world!", 5);
        assertThat(exception.getContext(), equalTo("Hello<--[HERE]"));
    }

    @Test
    public void getContext_long() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(mock(CommandExceptionType.class), new LiteralMessage("error"), "Hello world! This has an error in it. Oh dear!", 20);
        assertThat(exception.getContext(), equalTo("...d! This ha<--[HERE]"));
    }
}