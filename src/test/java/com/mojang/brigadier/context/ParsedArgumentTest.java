package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ParsedArgumentTest {
    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new ParsedArgument<>("foo", "bar"), new ParsedArgument<>("foo", "bar"))
            .addEqualityGroup(new ParsedArgument<>("bar", "baz"), new ParsedArgument<>("bar", "baz"))
            .addEqualityGroup(new ParsedArgument<>("foo", "baz"), new ParsedArgument<>("foo", "baz"))
            .testEquals();
    }

    @Test
    public void copy() throws Exception {
        final ParsedArgument<Object, String> argument = new ParsedArgument<>("foo", "bar");
        assertThat(argument.copy(), is(equalTo(argument)));
    }
}