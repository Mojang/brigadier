// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import org.junit.Before;
import org.junit.Test;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

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

    @Test
    public void testRedirect() throws Exception {
        final CommandNode<Object> target = mock(CommandNode.class);
        builder.redirect(target);
        assertThat(builder.getRedirect(), is(target));
    }

    @Test(expected = IllegalStateException.class)
    public void testRedirect_withChild() throws Exception {
        final CommandNode<Object> target = mock(CommandNode.class);
        builder.then(literal("foo"));
        builder.redirect(target);
    }

    @Test(expected = IllegalStateException.class)
    public void testThen_withRedirect() throws Exception {
        final CommandNode<Object> target = mock(CommandNode.class);
        builder.redirect(target);
        builder.then(literal("foo"));
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