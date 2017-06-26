package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
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

    @Before
    public void setUp() throws Exception {
        builder = new CommandContextBuilder<>(source);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_nonexistent() throws Exception {
        builder.build().getArgument("foo", Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_wrongType() throws Exception {
        CommandContext<Object> context = builder.withArgument("foo", integer().parse("123")).build();
        context.getArgument("foo", String.class);
    }

    @Test
    public void testGetArgument() throws Exception {
        CommandContext<Object> context = builder.withArgument("foo", integer().parse("123")).build();
        assertThat(context.getArgument("foo", int.class).getResult(), is(123));
    }

    @Test
    public void testSource() throws Exception {
        assertThat(builder.build().getSource(), is(source));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEquals() throws Exception {
        Object otherSource = new Object();
        Command<Object> command = mock(Command.class);
        Command<Object> otherCommand = mock(Command.class);
        CommandNode<Object> node = mock(CommandNode.class);
        CommandNode<Object> otherNode = mock(CommandNode.class);
        new EqualsTester()
            .addEqualityGroup(new CommandContextBuilder<>(source).build(), new CommandContextBuilder<>(source).build())
            .addEqualityGroup(new CommandContextBuilder<>(otherSource).build(), new CommandContextBuilder<>(otherSource).build())
            .addEqualityGroup(new CommandContextBuilder<>(source).withCommand(command).build(), new CommandContextBuilder<>(source).withCommand(command).build())
            .addEqualityGroup(new CommandContextBuilder<>(source).withCommand(otherCommand).build(), new CommandContextBuilder<>(source).withCommand(otherCommand).build())
            .addEqualityGroup(new CommandContextBuilder<>(source).withArgument("foo", integer().parse("123")).build(), new CommandContextBuilder<>(source).withArgument("foo", integer().parse("123")).build())
            .addEqualityGroup(new CommandContextBuilder<>(source).withNode(node, "foo").withNode(otherNode, "bar").build(), new CommandContextBuilder<>(source).withNode(node, "foo").withNode(otherNode, "bar").build())
            .addEqualityGroup(new CommandContextBuilder<>(source).withNode(otherNode, "bar").withNode(node, "foo").build(), new CommandContextBuilder<>(source).withNode(otherNode, "bar").withNode(node, "foo").build())
            .testEquals();
    }

    @Test
    public void testGetInput() throws Exception {
        CommandContext<Object> context = builder.withNode(literal("foo").build(), "foo").withNode(argument("bar", integer()).build(), "100").withNode(literal("baz").build(), "baz").build();

        assertThat(context.getInput(), is("foo 100 baz"));
    }
}