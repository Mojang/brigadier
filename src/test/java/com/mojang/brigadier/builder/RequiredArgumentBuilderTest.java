// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RequiredArgumentBuilderTest {
    @Mock
    private ArgumentType<Integer> type;
    private RequiredArgumentBuilder<Object, Integer> builder;
    @Mock
    private
    Command<Object> command;

    @Before
    public void setUp() throws Exception {
        builder = argument("foo", type);
    }

    @Test
    public void testBuild() throws Exception {
        final ArgumentCommandNode<Object, Integer> node = builder.build();

        assertThat(node.getName(), is("foo"));
        assertThat(node.getType(), is(type));
    }

    @Test
    public void testBuildWithExecutor() throws Exception {
        final ArgumentCommandNode<Object, Integer> node = builder.executes(command).build();

        assertThat(node.getName(), is("foo"));
        assertThat(node.getType(), is(type));
        assertThat(node.getCommand(), is(command));
    }

    @Test
    public void testBuildWithChildren() throws Exception {
        builder.then(argument("bar", integer()));
        builder.then(argument("baz", integer()));
        final ArgumentCommandNode<Object, Integer> node = builder.build();

        assertThat(node.getChildren(), hasSize(2));
    }
}