package com.mojang.brigadier.exceptions;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.StringReader;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class ParameterizedCommandExceptionTypeTest {
    private ParameterizedCommandExceptionType type;

    @Before
    public void setUp() throws Exception {
        type = new ParameterizedCommandExceptionType("foo", "Hello, ${name}!", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createMap_TooManyArguments() throws Exception {
        type.createMap("World", "Universe");
    }

    @Test
    public void createWithContext() throws Exception {
        final StringReader reader = new StringReader("Foo bar");
        reader.setCursor(5);
        final CommandException exception = type.createWithContext(reader, "World");
        assertThat(exception.getType(), is(type));
        assertThat(exception.getData(), is(ImmutableMap.<String, Object>of("name", "World")));
        assertThat(exception.getInput(), is("Foo bar"));
        assertThat(exception.getCursor(), is(5));
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new ParameterizedCommandExceptionType("foo", "Hello, world!"), new ParameterizedCommandExceptionType("foo", "Hello, universe!"), new ParameterizedCommandExceptionType("foo", "Hello, world!", "bar"))
            .addEqualityGroup(new ParameterizedCommandExceptionType("bar", "Hello, world!"), new ParameterizedCommandExceptionType("bar", "Hello, universe!"), new ParameterizedCommandExceptionType("bar", "Hello, world!", "bar"))
            .testEquals();
    }
}