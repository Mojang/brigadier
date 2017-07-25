package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import org.junit.Before;
import org.junit.Test;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class ArgumentBuilderTest {
    private TestableArgumentBuilder<Object> builder;

    @Before
    public void setUp() throws Exception {
        builder = new TestableArgumentBuilder<>();
    }

    @Test
    public void testArguments() throws Exception {
        final RequiredArgumentBuilder<Object, ?> argument = argument("bar", integer());

        builder.then(argument);

        assertThat(builder.getArguments(), hasSize(1));
        assertThat(builder.getArguments(), hasItem((CommandNode<Object>) argument.build()));
    }

    private static class TestableArgumentBuilder<S> extends ArgumentBuilder<S, TestableArgumentBuilder<S>> {
        @Override
        protected TestableArgumentBuilder<S> getThis() {
            return this;
        }

        @Override
        public CommandNode<S> build() {
            return null;
        }
    }
}