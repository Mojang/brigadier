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

import java.util.function.Function;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandContextTest {
    private CommandContextBuilder<Object> builder;
    @Mock
    private Object source;
    @Mock
    private CommandDispatcher<Object> dispatcher;

    @Before
    public void setUp() throws Exception {
        builder = new CommandContextBuilder<>(dispatcher, source);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_nonexistent() throws Exception {
        builder.build().getArgument("foo", Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_wrongType() throws Exception {
        CommandContext<Object> context = builder.withArgument("foo", integer().parse("123", new CommandContextBuilder<>(dispatcher, source))).build();
        context.getArgument("foo", String.class);
    }

    @Test
    public void testGetArgument() throws Exception {
        CommandContext<Object> context = builder.withArgument("foo", integer().parse("123", new CommandContextBuilder<>(dispatcher, source))).build();
        assertThat(context.getArgument("foo", int.class), is(123));
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
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source).build(), new CommandContextBuilder<>(dispatcher, source).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, otherSource).build(), new CommandContextBuilder<>(dispatcher, otherSource).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source).withCommand(command).build(), new CommandContextBuilder<>(dispatcher, source).withCommand(command).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source).withCommand(otherCommand).build(), new CommandContextBuilder<>(dispatcher, source).withCommand(otherCommand).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source).withArgument("foo", integer().parse("123", new CommandContextBuilder<>(dispatcher, source))).build(), new CommandContextBuilder<>(dispatcher, source).withArgument("foo", integer().parse("123", new CommandContextBuilder<>(dispatcher, source))).build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source).withNode(node, "foo").withNode(otherNode, "bar").build(), new CommandContextBuilder<>(dispatcher, source).withNode(node, "foo").withNode(otherNode, "bar").build())
            .addEqualityGroup(new CommandContextBuilder<>(dispatcher, source).withNode(otherNode, "bar").withNode(node, "foo").build(), new CommandContextBuilder<>(dispatcher, source).withNode(otherNode, "bar").withNode(node, "foo").build())
            .testEquals();
    }

    @Test
    public void testGetInput() throws Exception {
        CommandContext<Object> context = builder.withNode(literal("foo").build(), "foo").withNode(argument("bar", integer()).build(), "100").withNode(literal("baz").build(), "baz").build();

        assertThat(context.getInput(), is("foo 100 baz"));
    }

    @Test
    public void testCopy() throws Exception {
        Object first = new Object();
        Object second = new Object();
        @SuppressWarnings("unchecked") Function<Object, Object> supplier = (Function<Object, Object>) mock(Function.class);

        when(supplier.apply(source)).thenReturn(first);
        CommandContext<Object> context = builder.withNode(literal("test").build(), "test").withArgument("test", new DynamicParsedArgument<>("test", supplier)).build();
        assertThat(context.getArgument("test", Object.class), is(first));

        when(supplier.apply(source)).thenReturn(second);
        CommandContext<Object> copy = context.copy();
        assertThat(context, is(equalTo(copy)));
        assertThat(copy.getArgument("test", Object.class), is(second));
    }
}