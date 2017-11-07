package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
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

    @Before
    public void setUp() throws Exception {
        builder = new CommandContextBuilder<>(dispatcher, source, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_nonexistent() throws Exception {
        builder.build().getArgument("foo", Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_wrongType() throws Exception {
        final CommandContext<Object> context = builder.withArgument("foo", new ParsedArgument<>(0, 1, 123)).build();
        context.getArgument("foo", String.class);
    }

    @Test
    public void testGetArgument() throws Exception {
        final CommandContext<Object> context = builder.withArgument("foo", new ParsedArgument<>(0, 1, 123)).build();
        assertThat(context.getArgument("foo", int.class), is(123));
    }

    @Test
    public void testSource() throws Exception {
        assertThat(builder.build().getSource(), is(source));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEquals() throws Exception {
        final Object otherSource = new Object();
        final Command<Object> command = mock(Command.class);
        final Command<Object> otherCommand = mock(Command.class);
        final CommandNode<Object> node = mock(CommandNode.class);
        final CommandNode<Object> otherNode = mock(CommandNode.class);
        new EqualsTester()
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, 0).build(), new CommandContextBuilder<>(dispatcher, source, 0).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, otherSource, 0).build(), new CommandContextBuilder<>(dispatcher, otherSource, 0).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, 0).withCommand(command).build(), new CommandContextBuilder<>(dispatcher, source, 0).withCommand(command).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, 0).withCommand(otherCommand).build(), new CommandContextBuilder<>(dispatcher, source, 0).withCommand(otherCommand).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, 0).withArgument("foo", new ParsedArgument<>(0, 1, 123)).build(), new CommandContextBuilder<>(dispatcher, source, 0).withArgument("foo", new ParsedArgument<>(0, 1, 123)).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, 0).withNode(node, new StringRange(0, 3)).withNode(otherNode, new StringRange(4, 6)).build(), new CommandContextBuilder<>(dispatcher, source, 0).withNode(node, new StringRange(0, 3)).withNode(otherNode, new StringRange(4, 6)).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source, 0).withNode(otherNode, new StringRange(0, 3)).withNode(node, new StringRange(4, 6)).build(), new CommandContextBuilder<>(dispatcher, source, 0).withNode(otherNode, new StringRange(0, 3)).withNode(node, new StringRange(4, 6)).build())
            .testEquals();
    }
}