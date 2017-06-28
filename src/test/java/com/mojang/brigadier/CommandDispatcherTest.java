package com.mojang.brigadier;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherTest {
    private CommandDispatcher<Object> subject;
    @Mock
    private Command<Object> command;
    @Mock
    private Object source;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher<>();
        when(command.run(any())).thenReturn(42);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAndExecuteCommand() throws Exception {
        subject.register(literal("foo").executes(command));

        assertThat(subject.execute("foo", source), is(42));
        verify(command).run(any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAndMergeCommands() throws Exception {
        subject.register(literal("base").then(literal("foo")).executes(command));
        subject.register(literal("base").then(literal("bar")).executes(command));

        assertThat(subject.execute("base foo", source), is(42));
        assertThat(subject.execute("base bar", source), is(42));
        verify(command, times(2)).run(any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAndExecuteOverlappingCommands() throws Exception {
        Command<Object> one = mock(Command.class);
        Command<Object> two = mock(Command.class);
        Command<Object> three = mock(Command.class);

        when(one.run(any())).thenReturn(111);
        when(two.run(any())).thenReturn(222);
        when(three.run(any())).thenReturn(333);

        subject.register(
            literal("foo").then(
                argument("one", integer()).then(
                    literal("one").executes(one)
                )
            ).then(
                argument("two", integer()).then(
                    literal("two").executes(two)
                )
            ).then(
                argument("three", integer()).then(
                    literal("three").executes(three)
                )
            )
        );

        assertThat(subject.execute("foo 1 one", source), is(111));
        verify(one).run(any(CommandContext.class));

        assertThat(subject.execute("foo 2 two", source), is(222));
        verify(two).run(any(CommandContext.class));

        assertThat(subject.execute("foo 3 three", source), is(333));
        verify(three).run(any(CommandContext.class));
    }

    @Test
    public void testExecuteUnknownCommand() throws Exception {
        try {
            subject.execute("foo", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
        }
    }

    @Test
    public void testExecuteImpermissibleCommand() throws Exception {
        subject.register(literal("foo").requires(s -> false));

        try {
            subject.execute("foo", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_IMPERMISSIBLE));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
        }
    }

    @Test
    public void testExecuteEmptyCommand() throws Exception {
        subject.register(literal(""));

        try {
            subject.execute("", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
        }
    }

    @Test
    public void testExecuteUnknownSubcommand() throws Exception {
        subject.register(literal("foo").executes(command));

        try {
            subject.execute("foo bar", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteSubcommand() throws Exception {
        Command<Object> subCommand = mock(Command.class);
        when(subCommand.run(any())).thenReturn(100);

        subject.register(literal("foo").then(
            literal("a")
        ).then(
            literal("b").executes(subCommand)
        ).then(
            literal("c")
        ).executes(command));

        assertThat(subject.execute("foo b", source), is(100));
        verify(subCommand).run(any(CommandContext.class));
    }

    @Test
    public void testExecuteInvalidSubcommand() throws Exception {
        subject.register(literal("foo").then(
            argument("bar", integer())
        ).executes(command));

        try {
            subject.execute("foo bar", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_NOT_A_NUMBER));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", "bar")));
        }
    }
}