// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
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

    private static StringReader inputWithOffset(final String input, final int offset) {
        final StringReader result = new StringReader(input);
        result.setCursor(offset);
        return result;
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
    public void testCreateAndExecuteOffsetCommand() throws Exception {
        subject.register(literal("foo").executes(command));

        assertThat(subject.execute(inputWithOffset("/foo", 1), source), is(42));
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
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testExecuteImpermissibleCommand() throws Exception {
        subject.register(literal("foo").requires(s -> false));

        try {
            subject.execute("foo", source);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testExecuteEmptyCommand() throws Exception {
        subject.register(literal(""));

        try {
            subject.execute("", source);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testExecuteUnknownSubcommand() throws Exception {
        subject.register(literal("foo").executes(command));

        try {
            subject.execute("foo bar", source);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()));
            assertThat(ex.getCursor(), is(4));
        }
    }

    @Test
    public void testExecuteIncorrectLiteral() throws Exception {
        subject.register(literal("foo").executes(command).then(literal("bar")));

        try {
            subject.execute("foo baz", source);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()));
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
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()));
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
            literal("=").executes(subCommand)
        ).then(
            literal("c")
        ).executes(command));

        assertThat(subject.execute("foo =", source), is(100));
        verify(subCommand).run(any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParseIncompleteLiteral() throws Exception {
        subject.register(literal("foo").then(literal("bar").executes(command)));

        final ParseResults<Object> parse = subject.parse("foo ", source);
        assertThat(parse.getReader().getRemaining(), equalTo(" "));
        assertThat(parse.getContext().getNodes().size(), is(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParseIncompleteArgument() throws Exception {
        subject.register(literal("foo").then(argument("bar", integer()).executes(command)));

        final ParseResults<Object> parse = subject.parse("foo ", source);
        assertThat(parse.getReader().getRemaining(), equalTo(" "));
        assertThat(parse.getContext().getNodes().size(), is(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteAmbiguiousParentSubcommand() throws Exception {
        final Command<Object> subCommand = mock(Command.class);
        when(subCommand.run(any())).thenReturn(100);

        subject.register(
            literal("test")
                .then(
                    argument("incorrect", integer())
                        .executes(command)
                )
                .then(
                    argument("right", integer())
                        .then(
                            argument("sub", integer())
                                .executes(subCommand)
                        )
                )
        );

        assertThat(subject.execute("test 1 2", source), is(100));
        verify(subCommand).run(any(CommandContext.class));
        verify(command, never()).run(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteAmbiguiousParentSubcommandViaRedirect() throws Exception {
        final Command<Object> subCommand = mock(Command.class);
        when(subCommand.run(any())).thenReturn(100);

        final LiteralCommandNode<Object> real = subject.register(
            literal("test")
                .then(
                    argument("incorrect", integer())
                        .executes(command)
                )
                .then(
                    argument("right", integer())
                        .then(
                            argument("sub", integer())
                                .executes(subCommand)
                        )
                )
        );

        subject.register(literal("redirect").redirect(real));

        assertThat(subject.execute("redirect 1 2", source), is(100));
        verify(subCommand).run(any(CommandContext.class));
        verify(command, never()).run(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRedirectedMultipleTimes() throws Exception {
        final LiteralCommandNode<Object> concreteNode = subject.register(literal("actual").executes(command));
        final LiteralCommandNode<Object> redirectNode = subject.register(literal("redirected").redirect(subject.getRoot()));

        final String input = "redirected redirected actual";

        final ParseResults<Object> parse = subject.parse(input, source);
        assertThat(parse.getContext().getRange().get(input), equalTo("redirected"));
        assertThat(parse.getContext().getNodes().size(), is(1));
        assertThat(parse.getContext().getRootNode(), is(subject.getRoot()));
        assertThat(parse.getContext().getNodes().get(0).getRange(), equalTo(parse.getContext().getRange()));
        assertThat(parse.getContext().getNodes().get(0).getNode(), is(redirectNode));

        final CommandContextBuilder<Object> child1 = parse.getContext().getChild();
        assertThat(child1, is(notNullValue()));
        assertThat(child1.getRange().get(input), equalTo("redirected"));
        assertThat(child1.getNodes().size(), is(1));
        assertThat(child1.getRootNode(), is(subject.getRoot()));
        assertThat(child1.getNodes().get(0).getRange(), equalTo(child1.getRange()));
        assertThat(child1.getNodes().get(0).getNode(), is(redirectNode));

        final CommandContextBuilder<Object> child2 = child1.getChild();
        assertThat(child2, is(notNullValue()));
        assertThat(child2.getRange().get(input), equalTo("actual"));
        assertThat(child2.getNodes().size(), is(1));
        assertThat(child2.getRootNode(), is(subject.getRoot()));
        assertThat(child2.getNodes().get(0).getRange(), equalTo(child2.getRange()));
        assertThat(child2.getNodes().get(0).getNode(), is(concreteNode));

        assertThat(subject.execute(parse), is(42));
        verify(command).run(any(CommandContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteRedirected() throws Exception {
        final RedirectModifier<Object> modifier = mock(RedirectModifier.class);
        final Object source1 = new Object();
        final Object source2 = new Object();

        when(modifier.apply(argThat(hasProperty("source", is(source))))).thenReturn(Lists.newArrayList(source1, source2));

        final LiteralCommandNode<Object> concreteNode = subject.register(literal("actual").executes(command));
        final LiteralCommandNode<Object> redirectNode = subject.register(literal("redirected").fork(subject.getRoot(), modifier));

        final String input = "redirected actual";
        final ParseResults<Object> parse = subject.parse(input, source);
        assertThat(parse.getContext().getRange().get(input), equalTo("redirected"));
        assertThat(parse.getContext().getNodes().size(), is(1));
        assertThat(parse.getContext().getRootNode(), equalTo(subject.getRoot()));
        assertThat(parse.getContext().getNodes().get(0).getRange(), equalTo(parse.getContext().getRange()));
        assertThat(parse.getContext().getNodes().get(0).getNode(), is(redirectNode));
        assertThat(parse.getContext().getSource(), is(source));

        final CommandContextBuilder<Object> parent = parse.getContext().getChild();
        assertThat(parent, is(notNullValue()));
        assertThat(parent.getRange().get(input), equalTo("actual"));
        assertThat(parent.getNodes().size(), is(1));
        assertThat(parse.getContext().getRootNode(), equalTo(subject.getRoot()));
        assertThat(parent.getNodes().get(0).getRange(), equalTo(parent.getRange()));
        assertThat(parent.getNodes().get(0).getNode(), is(concreteNode));
        assertThat(parent.getSource(), is(source));

        assertThat(subject.execute(parse), is(2));
        verify(command).run(argThat(hasProperty("source", is(source1))));
        verify(command).run(argThat(hasProperty("source", is(source2))));
    }

    @Test
    public void testExecuteOrphanedSubcommand() throws Exception {
        subject.register(literal("foo").then(
            argument("bar", integer())
        ).executes(command));

        try {
            subject.execute("foo 5", source);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            assertThat(ex.getCursor(), is(5));
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
            subject.execute("foo$", source);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()));
            assertThat(ex.getCursor(), is(0));
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
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt()));
            assertThat(ex.getCursor(), is(4));
        }
    }

    @Test
    public void testGetPath() {
        final LiteralCommandNode<Object> bar = literal("bar").build();
        subject.register(literal("foo").then(bar));

        assertThat(subject.getPath(bar), equalTo(Lists.newArrayList("foo", "bar")));
    }

    @Test
    public void testFindNodeExists() {
        final LiteralCommandNode<Object> bar = literal("bar").build();
        subject.register(literal("foo").then(bar));

        assertThat(subject.findNode(Lists.newArrayList("foo", "bar")), is(bar));
    }

    @Test
    public void testFindNodeDoesntExist() {
        assertThat(subject.findNode(Lists.newArrayList("foo", "bar")), is(nullValue()));
    }
}