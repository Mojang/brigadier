package com.mojang.brigadier.exceptions;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SimpleCommandExceptionTypeTest {
    @Test
    public void testCreate() throws Exception {
        SimpleCommandExceptionType type = new SimpleCommandExceptionType("foo", "bar");
        CommandException exception = type.create();
        assertThat(exception.getType(), is(type));
        assertThat(exception.getMessage(), is("bar"));
        assertThat(exception.getData().values(), empty());
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new SimpleCommandExceptionType("foo", "Hello, world!"), new SimpleCommandExceptionType("foo", "Hello, universe!"))
            .addEqualityGroup(new SimpleCommandExceptionType("bar", "Hello, world!"), new SimpleCommandExceptionType("bar", "Hello, universe!"))
            .testEquals();
    }
}