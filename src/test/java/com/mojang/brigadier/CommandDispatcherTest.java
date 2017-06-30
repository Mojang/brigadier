package com.mojang.brigadier;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
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
        subject.register(literal("base").then(literal("foo").executes(command)));
        subject.register(literal("base").then(literal("bar").executes(command)));

        assertThat(subject.execute("base foo", source), is(42));
        assertThat(subject.execute("base bar", source), is(42));
        verify(command, times(2)).run(any(CommandContext.class));
    }

    @Test
    public void testExecuteUnknownCommand() throws Exception {
        subject.register(literal("bar"));
        subject.register(literal("baz"));

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
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
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
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_ARGUMENT));
            assertThat(ex.getData(), is(Collections.singletonMap("argument", "bar")));
        }
    }

    @Test
    public void testExecuteIncorrectLiteral() throws Exception {
        subject.register(literal("foo").executes(command).then(literal("bar")));

        try {
            subject.execute("foo baz", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(LiteralCommandNode.ERROR_INCORRECT_LITERAL));
            assertThat(ex.getData(), is(Collections.singletonMap("expected", "bar")));
        }
    }

    @Test
    public void testExecuteAmbiguousIncorrectArgument() throws Exception {
        subject.register(
            literal("foo").executes(command)
                .then(literal("bar"))
                .then(literal("baz"))
        );

        try {
            subject.execute("foo unknown", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_ARGUMENT));
            assertThat(ex.getData(), is(Collections.singletonMap("argument", "unknown")));
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