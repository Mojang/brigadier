// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.dispatching.DispatchingState;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteRecursiveCommand() throws Exception {
        final int diff = (int)(Math.random() * 1024);
        final Command<Object> calledCommand = mock(Command.class);
        final RecursiveCommand<Object, DispatchingState<Object>> recursiveCallerCommand = spy(new RecursiveCommand<Object, DispatchingState<Object>>() {
            @Override
            public DispatchingState<Object> start(final CommandContext<Object> context) throws CommandSyntaxException {
                return subject.executeCumulative("called", context.getSource());
            }

            @Override
            public int finish(final CommandContext<Object> context, final DispatchingState<Object> intermediate) throws CommandSyntaxException {
                return intermediate.getReturnValue() + diff;
            }
        });
        final RecursiveCommand<Object, List<DispatchingState<Object>>> fatherCallerCommand = spy(new RecursiveCommand<Object, List<DispatchingState<Object>>>() {
            @Override
            public List<DispatchingState<Object>> start(final CommandContext<Object> context) throws CommandSyntaxException {
                return Lists.newArrayList(subject.executeCumulative("recursive-caller", context.getSource()), subject.executeCumulative("called", context.getSource()));
            }

            @Override
            public int finish(final CommandContext<Object> context, final List<DispatchingState<Object>> intermediate) throws CommandSyntaxException {
                int ret = 1;
                for (final DispatchingState<Object> each : intermediate) {
                    ret *= each.getReturnValue();
                }
                return ret;
            }
        });
        final ResultConsumer<Object> consumer = mock(ResultConsumer.class);

        subject.setConsumer(consumer);
        subject.register(literal("called").executes(calledCommand));
        subject.register(literal("recursive-caller").executes(recursiveCallerCommand));
        final LiteralCommandNode<Object> fatherCallerNode = subject.register(literal("father-caller").executes(fatherCallerCommand));

        final int baseCalledResult = (int)(Math.random() * 1024);
        when(calledCommand.run(any())).thenReturn(baseCalledResult);
        assertThat(subject.execute("called", source), is(baseCalledResult));
        verify(calledCommand).run(any());
        verify(consumer).onCommandComplete(any(), eq(true), eq(baseCalledResult));
        verify(consumer, never()).onCommandComplete(any(), eq(false), anyInt());
        reset(calledCommand, recursiveCallerCommand, fatherCallerCommand, consumer);

        final int simpleResult = (int)(Math.random() * 1024);
        when(calledCommand.run(any())).thenReturn(simpleResult);
        assertThat(subject.execute("recursive-caller", source), is(simpleResult + diff));
        final InOrder simpleInOrder = inOrder(calledCommand, recursiveCallerCommand, consumer);
        simpleInOrder.verify(recursiveCallerCommand).start(any());
        simpleInOrder.verify(calledCommand).run(any());
        simpleInOrder.verify(consumer).onCommandComplete(any(), eq(true), eq(simpleResult));
        simpleInOrder.verify(recursiveCallerCommand).finish(any(), any());
        simpleInOrder.verify(consumer).onCommandComplete(any(), eq(true), eq(simpleResult + diff));
        verify(consumer, never()).onCommandComplete(any(), eq(false), anyInt());
        reset(calledCommand, recursiveCallerCommand, fatherCallerCommand, consumer);

        final int embeddedResult = (int)(Math.random() * 1024);
        when(calledCommand.run(any())).thenReturn(embeddedResult);
        assertThat(subject.execute("father-caller", source), is(embeddedResult * (embeddedResult + diff)));
        final InOrder embeddedOrder = inOrder(calledCommand, recursiveCallerCommand, fatherCallerCommand, consumer);
        embeddedOrder.verify(fatherCallerCommand).start(any());
        embeddedOrder.verify(recursiveCallerCommand).start(any());
        embeddedOrder.verify(calledCommand).run(any());
        embeddedOrder.verify(consumer).onCommandComplete(any(), eq(true), eq(embeddedResult));
        embeddedOrder.verify(recursiveCallerCommand).finish(any(), any());
        embeddedOrder.verify(consumer).onCommandComplete(any(), eq(true), eq(embeddedResult + diff));
        embeddedOrder.verify(calledCommand).run(any());
        embeddedOrder.verify(consumer).onCommandComplete(any(), eq(true), eq(embeddedResult));
        embeddedOrder.verify(fatherCallerCommand).finish(any(), any());
        embeddedOrder.verify(consumer).onCommandComplete(any(), eq(true), eq(embeddedResult * (embeddedResult + diff)));
        verify(consumer, never()).onCommandComplete(any(), eq(false), anyInt());
        reset(calledCommand, recursiveCallerCommand, fatherCallerCommand, consumer);

        final RedirectModifier<Object> modifier = mock(RedirectModifier.class);
        final Object source1 = new Object();
        final Object source2 = new Object();

        final int firstRedirectedResult = (int)(Math.random() * 1024);
        final int secondRedirectedResult = (int)(Math.random() * 1024);
        final int thirdRedirectedResult = (int)(Math.random() * 1024);
        final int fourthRedirectedResult = (int)(Math.random() * 1024);
        when(calledCommand.run(any())).thenReturn(firstRedirectedResult, secondRedirectedResult, thirdRedirectedResult, fourthRedirectedResult);
        when(modifier.apply(argThat(hasProperty("source", is(source))))).thenReturn(Lists.newArrayList(source1, source2));

        final LiteralCommandNode<Object> redirectNode = subject.register(literal("redirected").fork(subject.getRoot(), modifier));

        final String input = "redirected father-caller";
        final ParseResults<Object> parse = subject.parse(input, source);
        assertThat(parse.getContext().getRange().get(input), equalTo("redirected"));
        assertThat(parse.getContext().getNodes().size(), is(1));
        assertThat(parse.getContext().getRootNode(), equalTo(subject.getRoot()));
        assertThat(parse.getContext().getNodes().get(0).getRange(), equalTo(parse.getContext().getRange()));
        assertThat(parse.getContext().getNodes().get(0).getNode(), is(redirectNode));
        assertThat(parse.getContext().getSource(), is(source));

        final CommandContextBuilder<Object> parent = parse.getContext().getChild();
        assertThat(parent, is(notNullValue()));
        assertThat(parent.getRange().get(input), equalTo("father-caller"));
        assertThat(parent.getNodes().size(), is(1));
        assertThat(parse.getContext().getRootNode(), equalTo(subject.getRoot()));
        assertThat(parent.getNodes().get(0).getRange(), equalTo(parent.getRange()));
        assertThat(parent.getNodes().get(0).getNode(), is(fatherCallerNode));
        assertThat(parent.getSource(), is(source));

        subject.setConsumer(consumer);
        assertThat(subject.execute(parse), is(2));

        final InOrder redirectedOrder = inOrder(calledCommand, recursiveCallerCommand, fatherCallerCommand, consumer);

        redirectedOrder.verify(fatherCallerCommand).start(argThat(hasProperty("source", is(source1))));
        redirectedOrder.verify(recursiveCallerCommand).start(argThat(hasProperty("source", is(source1))));
        redirectedOrder.verify(calledCommand).run(argThat(hasProperty("source", is(source1))));
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source1))), eq(true), eq(firstRedirectedResult));
        redirectedOrder.verify(recursiveCallerCommand).finish(argThat(hasProperty("source", is(source1))), any());
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source1))), eq(true), eq(firstRedirectedResult + diff));
        redirectedOrder.verify(calledCommand).run(argThat(hasProperty("source", is(source1))));
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source1))), eq(true), eq(secondRedirectedResult));
        redirectedOrder.verify(fatherCallerCommand).finish(argThat(hasProperty("source", is(source1))), any());
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source1))), eq(true), eq((firstRedirectedResult + diff) * secondRedirectedResult));

        redirectedOrder.verify(fatherCallerCommand).start(argThat(hasProperty("source", is(source2))));
        redirectedOrder.verify(recursiveCallerCommand).start(argThat(hasProperty("source", is(source2))));
        redirectedOrder.verify(calledCommand).run(argThat(hasProperty("source", is(source2))));
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source2))), eq(true), eq(thirdRedirectedResult));
        redirectedOrder.verify(recursiveCallerCommand).finish(argThat(hasProperty("source", is(source2))), any());
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source2))), eq(true), eq(thirdRedirectedResult + diff));
        redirectedOrder.verify(calledCommand).run(argThat(hasProperty("source", is(source2))));
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source2))), eq(true), eq(fourthRedirectedResult));
        redirectedOrder.verify(fatherCallerCommand).finish(argThat(hasProperty("source", is(source2))), any());
        redirectedOrder.verify(consumer).onCommandComplete(argThat(hasProperty("source", is(source2))), eq(true), eq((thirdRedirectedResult + diff) * fourthRedirectedResult));

        verify(consumer, never()).onCommandComplete(any(), eq(false), anyInt());
    }
}