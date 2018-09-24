// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class DynamicCommandSyntaxExceptionTypeTest {
    private DynamicCommandExceptionType type;

    @Before
    public void setUp() throws Exception {
        type = new DynamicCommandExceptionType(name -> new LiteralMessage("Hello, " + name + "!"));
    }

    @Test
    public void createWithContext() throws Exception {
        final StringReader reader = new StringReader("Foo bar");
        reader.setCursor(5);
        final CommandSyntaxException exception = type.createWithContext(reader, "World");
        assertThat(exception.getType(), is(type));
        assertThat(exception.getInput(), is("Foo bar"));
        assertThat(exception.getCursor(), is(5));
    }
}