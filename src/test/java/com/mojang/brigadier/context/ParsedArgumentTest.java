package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

public class ParsedArgumentTest {
    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new ParsedArgument<>("foo", "bar"), new ParsedArgument<>("foo", "bar"))
            .addEqualityGroup(new ParsedArgument<>("bar", "baz"), new ParsedArgument<>("bar", "baz"))
            .addEqualityGroup(new ParsedArgument<>("foo", "baz"), new ParsedArgument<>("foo", "baz"))
            .testEquals();
    }
}