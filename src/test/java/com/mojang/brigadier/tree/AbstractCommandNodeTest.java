// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCommandNodeTest {
    @Mock
    private Command command;

    protected abstract CommandNode<Object> getCommandNode();

    @Test
    public void testAddChild() throws Exception {
        final CommandNode<Object> node = getCommandNode();

        node.addChild(literal("child1").build());
        node.addChild(literal("child2").build());
        node.addChild(literal("child1").build());

        assertThat(node.getChildren(), hasSize(2));
    }

    @Test
    public void testAddChildMergesGrandchildren() throws Exception {
        final CommandNode<Object> node = getCommandNode();

        node.addChild(literal("child").then(
            literal("grandchild1")
        ).build());

        node.addChild(literal("child").then(
            literal("grandchild2")
        ).build());

        assertThat(node.getChildren(), hasSize(1));
        assertThat(node.getChildren().iterator().next().getChildren(), hasSize(2));
    }

    @Test
    public void testAddChildPreservesCommand() throws Exception {
        final CommandNode<Object> node = getCommandNode();

        node.addChild(literal("child").executes(command).build());
        node.addChild(literal("child").build());

        assertThat(node.getChildren().iterator().next().getCommand(), is(command));
    }

    @Test
    public void testAddChildOverwritesCommand() throws Exception {
        final CommandNode<Object> node = getCommandNode();

        node.addChild(literal("child").build());
        node.addChild(literal("child").executes(command).build());

        assertThat(node.getChildren().iterator().next().getCommand(), is(command));
    }
}
