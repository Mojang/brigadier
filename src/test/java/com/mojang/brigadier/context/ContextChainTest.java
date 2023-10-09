package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.CommandDispatcherTest;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextChainTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteAllForSingleCommand() throws CommandSyntaxException {
        final ResultConsumer<Object> consumer = mock(ResultConsumer.class);
        final Command<Object> command = mock(Command.class);

        when(command.run(any())).thenReturn(4);

        final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("foo").executes(command));
        final Object source = "compile_source";

        final ParseResults<Object> result = dispatcher.parse("foo", source);
        final CommandContext<Object> topContext = result.getContext().build("foo");
        final ContextChain<Object> chain = ContextChain.tryFlatten(topContext).orElseThrow(AssertionError::new);

        final Object runtimeSource = "runtime_source";
        assertThat(chain.executeAll(runtimeSource, consumer), is(4));

        verify(command).run(argThat(CommandDispatcherTest.contextSourceMatches(runtimeSource)));

        verify(consumer).onCommandComplete(argThat(CommandDispatcherTest.contextSourceMatches(runtimeSource)), eq(true), eq(4));
        verifyNoMoreInteractions(consumer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteAllForRedirectedCommand() throws CommandSyntaxException {
        final ResultConsumer<Object> consumer = mock(ResultConsumer.class);
        final Command<Object> command = mock(Command.class);

        when(command.run(any())).thenReturn(4);

        final Object redirectedSource = "redirected_source";

        final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("foo").executes(command));
        dispatcher.register(literal("bar").redirect(dispatcher.getRoot(), context -> redirectedSource));
        final Object source = "compile_source";

        final ParseResults<Object> result = dispatcher.parse("bar foo", source);
        final CommandContext<Object> topContext = result.getContext().build("bar foo");
        final ContextChain<Object> chain = ContextChain.tryFlatten(topContext).orElseThrow(AssertionError::new);

        final Object runtimeSource = "runtime_source";
        assertThat(chain.executeAll(runtimeSource, consumer), is(4));

        verify(command).run(argThat(CommandDispatcherTest.contextSourceMatches(redirectedSource)));

        verify(consumer).onCommandComplete(argThat(CommandDispatcherTest.contextSourceMatches(redirectedSource)), eq(true), eq(4));
        verifyNoMoreInteractions(consumer);
    }

    @Test
    public void testSingleStageExecution() {
        final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("foo").executes(context -> 1));
        final Object source = new Object();

        final ParseResults<Object> result = dispatcher.parse("foo", source);
        final CommandContext<Object> topContext = result.getContext().build("foo");
        final ContextChain<Object> chain = ContextChain.tryFlatten(topContext).orElseThrow(AssertionError::new);

        assertThat(chain.getStage(), is(ContextChain.Stage.EXECUTE));
        assertThat(chain.getTopContext(), is(topContext));
        assertThat(chain.nextStage(), nullValue());
    }

    @Test
    public void testMultiStageExecution() {
        final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("foo").executes(context -> 1));
        dispatcher.register(literal("bar").redirect(dispatcher.getRoot()));
        final Object source = new Object();

        final ParseResults<Object> result = dispatcher.parse("bar bar foo", source);
        final CommandContext<Object> topContext = result.getContext().build("bar bar foo");
        final ContextChain<Object> stage0 = ContextChain.tryFlatten(topContext).orElseThrow(AssertionError::new);

        assertThat(stage0.getStage(), is(ContextChain.Stage.MODIFY));
        assertThat(stage0.getTopContext(), is(topContext));

        final ContextChain<Object> stage1 = stage0.nextStage();
        assertThat(stage1, notNullValue());
        assertThat(stage1.getStage(), is(ContextChain.Stage.MODIFY));
        assertThat(stage1.getTopContext(), is(topContext.getChild()));

        final ContextChain<Object> stage2 = stage1.nextStage();
        assertThat(stage2, notNullValue());
        assertThat(stage2.getStage(), is(ContextChain.Stage.EXECUTE));
        assertThat(stage2.getTopContext(), is(topContext.getChild().getChild()));

        assertThat(stage2.nextStage(), nullValue());
    }

    @Test
    public void testMissingExecute() {
        final CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("foo").executes(context -> 1));
        dispatcher.register(literal("bar").redirect(dispatcher.getRoot()));

        final Object source = new Object();
        final ParseResults<Object> result = dispatcher.parse("bar bar", source);
        final CommandContext<Object> topContext = result.getContext().build("bar bar");
        assertThat(ContextChain.tryFlatten(topContext), is(Optional.empty()));
    }
}
