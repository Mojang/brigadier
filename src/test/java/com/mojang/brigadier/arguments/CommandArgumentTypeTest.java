package com.mojang.brigadier.arguments;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.mojang.brigadier.arguments.CommandArgumentType.command;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandArgumentTypeTest {
    @Mock
    private Object source;
    @Mock
    private CommandDispatcher<Object> dispatcher;

    @SuppressWarnings("unchecked")
    @Test
    public void testParse() throws Exception {
        final ParseResults<Object> command = mock(ParseResults.class);
        when(dispatcher.parse("hello world", source)).thenReturn(command);
        final ParsedArgument<Object, ParseResults<Object>> argument = command().parse("hello world", new CommandContextBuilder<>(dispatcher, source));
        assertThat(argument.getRaw(), equalTo("hello world"));
        assertThat(argument.getResult(source), is(command));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParse_fail() throws Exception {
        final CommandException thrown = mock(CommandException.class);
        when(dispatcher.parse("hello world", source)).thenThrow(thrown);
        try {
            command().parse("hello world", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException exception) {
            assertThat(exception, is(thrown));
        }
    }

    @Test
    public void listSuggestions() throws Exception {
        Set<String> output = Sets.newHashSet();
        when(dispatcher.getCompletionSuggestions("foo bar baz", source)).thenReturn(new String[] {"a", "b"});
        command().listSuggestions("foo bar baz", output, new CommandContextBuilder<>(dispatcher, source));
        verify(dispatcher).getCompletionSuggestions("foo bar baz", source);
        assertThat(output, equalTo(ImmutableSet.of("a", "b")));
    }
}