package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FixedParsedArgumentTest {
    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new FixedParsedArgument<>("foo", "bar"), new FixedParsedArgument<>("foo", "bar"))
            .addEqualityGroup(new FixedParsedArgument<>("bar", "baz"), new FixedParsedArgument<>("bar", "baz"))
            .addEqualityGroup(new FixedParsedArgument<>("foo", "baz"), new FixedParsedArgument<>("foo", "baz"))
            .testEquals();
    }

    @Test
    public void copy() throws Exception {
        final FixedParsedArgument<String> argument = new FixedParsedArgument<>("foo", "bar");
        assertThat(argument.copy(), is(equalTo(argument)));
    }
}