package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import org.junit.Before;
import org.junit.Test;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class ArgumentBuilderTest {
    private TestableArgumentBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new TestableArgumentBuilder();
    }

    @Test
    public void testArguments() throws Exception {
        RequiredArgumentBuilder argument = argument("bar", integer());

        builder.then(argument);

        assertThat(builder.getArguments(), hasSize(1));
        assertThat(builder.getArguments(), hasItems((CommandNode) argument.build()));
    }

    private static class TestableArgumentBuilder extends ArgumentBuilder<TestableArgumentBuilder> {
        @Override
        protected TestableArgumentBuilder getThis() {
            return this;
        }

        @Override
        public CommandNode build() {
            return null;
        }
    }
}