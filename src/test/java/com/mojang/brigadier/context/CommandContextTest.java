// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CommandContextTest {
    private CommandContextBuilder<Object> builder;
    @Mock
    private Object source;
    @Mock
    private CommandDispatcher<Object> dispatcher;

    @Mock
    private CommandNode<Object> rootNode;

    @Before
    public void setUp() throws Exception {
        builder = new CommandContextBuilder<>(dispatcher, source, rootNode, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_nonexistent() throws Exception {
        builder.build("").getArgument("foo", Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_wrongType() throws Exception {
        final CommandContext<Object> context = builder.withArgument("foo", new ParsedArgument<>(0, 1, 123)).build("123");
        context.getArgument("foo", String.class);
    }

    @Test
    public void testGetArgument() throws Exception {
        final CommandContext<Object> context = builder.withArgument("foo", new ParsedArgument<>(0, 1, 123)).build("123");
        assertThat(context.getArgument("foo", int.class), is(123));
    }

    @Test
    public void testCopyArgumentsFills() throws Exception {
        final CommandContext<Object> originalContext = new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument<>(0, 1, 123)).build("123");
        final CommandContext<Object> childContext = new CommandContextBuilder<>(dispatcher, source, rootNode, 0).build("123");

        final CommandContext<Object> copiedContext = childContext.copyWithArgumentsOf(originalContext);
        assertThat(copiedContext.getArgument("foo", int.class), is(123));
    }

    @Test
    public void testCopyArgumentsOverrides() throws Exception {
        final CommandContext<Object> originalContext = new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument<>(0, 1, 123)).build("123");
        final CommandContext<Object> childContext = new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument<>(0, 1, "123")).build("123");

        final CommandContext<Object> copiedContext = childContext.copyWithArgumentsOf(originalContext);
        assertThat(copiedContext.getArgument("foo", String.class), is("123"));
    }

    @Test
    public void testCopyArgumentsMerges() throws Exception {
        final CommandContext<Object> originalContext = new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument<>(0, 1, 123)).build("123");
        final CommandContext<Object> childContext = new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withArgument("bar", new ParsedArgument<>(0, 1, "123")).build("123");

        final CommandContext<Object> copiedContext = childContext.copyWithArgumentsOf(originalContext);
        assertThat(copiedContext.getArgument("foo", int.class), is(123));
        assertThat(copiedContext.getArgument("bar", String.class), is("123"));
    }

    @Test
    public void testSource() throws Exception {
        assertThat(builder.build("").getSource(), is(source));
    }

    @Test
    public void testRootNode() throws Exception {
        assertThat(builder.build("").getRootNode(), is(rootNode));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEquals() throws Exception {
        final Object otherSource = new Object();
        final Command<Object> command = mock(Command.class);
        final Command<Object> otherCommand = mock(Command.class);
        final CommandNode<Object> rootNode = mock(CommandNode.class);
        final CommandNode<Object> otherRootNode = mock(CommandNode.class);
        final CommandNode<Object> node = mock(CommandNode.class);
        final CommandNode<Object> otherNode = mock(CommandNode.class);
        new EqualsTester()
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, rootNode, 0).build(""), new CommandContextBuilder<>(dispatcher, source, rootNode, 0).build(""))
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, otherRootNode, 0).build(""), new CommandContextBuilder<>(dispatcher, source, otherRootNode, 0).build(""))
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, otherSource, rootNode, 0).build(""), new CommandContextBuilder<>(dispatcher, otherSource, rootNode, 0).build(""))
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withCommand(command).build(""), new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withCommand(command).build(""))
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withCommand(otherCommand).build(""), new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withCommand(otherCommand).build(""))
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument<>(0, 1, 123)).build("123"), new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withArgument("foo", new ParsedArgument<>(0, 1, 123)).build("123"))
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withNode(node, StringRange.between(0, 3)).withNode(otherNode, StringRange.between(4, 6)).build("123 456"), new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withNode(node, StringRange.between(0, 3)).withNode(otherNode, StringRange.between(4, 6)).build("123 456"))
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withNode(otherNode, StringRange.between(0, 3)).withNode(node, StringRange.between(4, 6)).build("123 456"), new CommandContextBuilder<>(dispatcher, source, rootNode, 0).withNode(otherNode, StringRange.between(0, 3)).withNode(node, StringRange.between(4, 6)).build("123 456"))
            .testEquals();
    }
}