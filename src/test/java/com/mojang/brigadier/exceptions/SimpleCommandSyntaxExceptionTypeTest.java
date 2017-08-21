package com.mojang.brigadier.exceptions;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.StringReader;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SimpleCommandSyntaxExceptionTypeTest {
    @Test
    public void createWithContext() throws Exception {
        final SimpleCommandExceptionType type = new SimpleCommandExceptionType("foo", "bar");
        final StringReader reader = new StringReader("Foo bar");
        reader.setCursor(5);
        final CommandSyntaxException exception = type.createWithContext(reader);
        assertThat(exception.getType(), is(type));
        assertThat(exception.getData(), is(Collections.emptyMap()));
        assertThat(exception.getInput(), is("Foo bar"));
        assertThat(exception.getCursor(), is(5));
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new SimpleCommandExceptionType("foo", "Hello, world!"), new SimpleCommandExceptionType("foo", "Hello, universe!"))
            .addEqualityGroup(new SimpleCommandExceptionType("bar", "Hello, world!"), new SimpleCommandExceptionType("bar", "Hello, universe!"))
            .testEquals();
    }

    @Test
    public void getContext_none() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(mock(CommandExceptionType.class), Collections.emptyMap());
        assertThat(exception.getContext(), is(nullValue()));
    }

    @Test
    public void getContext_short() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(mock(CommandExceptionType.class), Collections.emptyMap(), "Hello world!", 5);
        assertThat(exception.getContext(), equalTo("Hello<--[HERE]"));
    }

    @Test
    public void getContext_long() throws Exception {
        final CommandSyntaxException exception = new CommandSyntaxException(mock(CommandExceptionType.class), Collections.emptyMap(), "Hello world! This has an error in it. Oh dear!", 20);
        assertThat(exception.getContext(), equalTo("...d! This ha<--[HERE]"));
    }
}