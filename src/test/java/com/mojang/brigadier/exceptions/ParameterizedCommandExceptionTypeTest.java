package com.mojang.brigadier.exceptions;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class ParameterizedCommandExceptionTypeTest {
    ParameterizedCommandExceptionType type;

    @Before
    public void setUp() throws Exception {
        type = new ParameterizedCommandExceptionType("foo", "Hello, ${name}!", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTooFewArguments() throws Exception {
        type.create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTooManyArguments() throws Exception {
        type.create("World", "Universe");
    }

    @Test
    public void testCreate() throws Exception {
        CommandException exception = type.create("World");
        assertThat(exception.getType(), is((CommandExceptionType) type));
        assertThat(exception.getData(), is((Map<String, Object>) ImmutableMap.<String, Object>of("name", "World")));
        assertThat(exception.getMessage(), is("Hello, World!"));
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new ParameterizedCommandExceptionType("foo", "Hello, world!"), new ParameterizedCommandExceptionType("foo", "Hello, universe!"), new ParameterizedCommandExceptionType("foo", "Hello, world!", "bar"))
            .addEqualityGroup(new ParameterizedCommandExceptionType("bar", "Hello, world!"), new ParameterizedCommandExceptionType("bar", "Hello, universe!"), new ParameterizedCommandExceptionType("bar", "Hello, world!", "bar"))
            .testEquals();
    }
}