package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testExecuteImpermissibleCommand() throws Exception {
        subject.register(literal("foo").requires(s -> false));

        try {
            subject.execute("foo", source);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testExecuteEmptyCommand() throws Exception {
        subject.register(literal(""));

        try {
            subject.execute("", source);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testExecuteUnknownSubcommand() throws Exception {
        subject.register(literal("foo").executes(command));

        try {
            subject.execute("foo bar", source);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_ARGUMENT));
            assertThat(ex.getData(), is(Collections.emptyMap()));
            assertThat(ex.getCursor(), is(4));
        }
    }

    @Test
    public void testExecuteIncorrectLiteral() throws Exception {
        subject.register(literal("foo").executes(command).then(literal("bar")));

        try {
            subject.execute("foo baz", source);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(LiteralCommandNode.ERROR_INCORRECT_LITERAL));
            assertThat(ex.getData(), is(Collections.singletonMap("expected", "bar")));
            assertThat(ex.getCursor(), is(4));
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
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_ARGUMENT));
            assertThat(ex.getData(), is(Collections.emptyMap()));
            assertThat(ex.getCursor(), is(4));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteSubcommand() throws Exception {
        final Command<Object> subCommand = mock(Command.class);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRedirected() throws Exception {
        subject.register(literal("actual").executes(command));
        subject.register(literal("redirected").redirect(subject.getRoot()));

        final ParseResults<Object> parse = subject.parse("redirected redirected actual", source);
        assertThat(parse.getContext().getInput(), equalTo("actual"));
        assertThat(parse.getContext().getNodes().size(), is(2));

        final CommandContext<Object> parent1 = parse.getContext().getParent();
        assertThat(parent1, is(notNullValue()));
        assertThat(parent1.getInput(), equalTo("redirected"));
        assertThat(parent1.getNodes().size(), is(2));

        final CommandContext<Object> parent2 = parent1.getParent();
        assertThat(parent2, is(notNullValue()));
        assertThat(parent2.getInput(), equalTo("redirected"));
        assertThat(parent2.getNodes().size(), is(1));

        assertThat(subject.execute(parse), is(42));
        verify(command).run(any(CommandContext.class));
    }

    @Test
    public void testExecuteOrphanedSubcommand() throws Exception {
        subject.register(literal("foo").then(
            argument("bar", integer())
        ).executes(command));

        try {
            subject.execute("foo 5", source);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.emptyMap()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testExecute_invalidOther() throws Exception {
        final Command<Object> wrongCommand = mock(Command.class);
        subject.register(literal("w").executes(wrongCommand));
        subject.register(literal("world").executes(command));

        assertThat(subject.execute("world", source), is(42));
        verify(wrongCommand, never()).run(any());
        verify(command).run(any());
    }

    @Test
    public void parse_noSpaceSeparator() throws Exception {
        subject.register(literal("foo").then(argument("bar", integer()).executes(command)));

        try {
            subject.execute("foo5", source);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_EXPECTED_ARGUMENT_SEPARATOR));
            assertThat(ex.getData(), is(Collections.emptyMap()));
            assertThat(ex.getCursor(), is(3));
        }
    }

    @Test
    public void testExecuteInvalidSubcommand() throws Exception {
        subject.register(literal("foo").then(
            argument("bar", integer())
        ).executes(command));

        try {
            subject.execute("foo bar", source);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(StringReader.ERROR_EXPECTED_INT));
            assertThat(ex.getData(), is(Collections.emptyMap()));
            assertThat(ex.getCursor(), is(4));
        }
    }
}