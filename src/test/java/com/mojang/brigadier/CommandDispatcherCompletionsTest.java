package com.mojang.brigadier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherCompletionsTest {
    private CommandDispatcher<Object> subject;
    @Mock
    private Object source;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher<>();
    }

    @Test
    public void testNoCommands() throws Exception {
        assertThat(subject.getCompletionSuggestions("", source), is(emptyArray()));
    }

    @Test
    public void testCommand() throws Exception {
        subject.register(literal("foo"));
        subject.register(literal("bar"));
        subject.register(literal("baz").requires(s -> false));
        assertThat(subject.getCompletionSuggestions("", source), equalTo(new String[] {"foo", "bar"}));
        assertThat(subject.getCompletionSuggestions("f", source), equalTo(new String[] {"foo"}));
        assertThat(subject.getCompletionSuggestions("b", source), equalTo(new String[] {"bar"}));
        assertThat(subject.getCompletionSuggestions("q", source), is(emptyArray()));
    }

    @Test
    public void testSubCommand() throws Exception {
        subject.register(literal("foo").then(literal("abc")).then(literal("def")).then(literal("ghi").requires(s -> false)));
        subject.register(literal("bar"));
        assertThat(subject.getCompletionSuggestions("", source), equalTo(new String[] {"foo", "bar"}));
        assertThat(subject.getCompletionSuggestions("f", source), equalTo(new String[] {"foo"}));
        assertThat(subject.getCompletionSuggestions("foo", source), equalTo(new String[] {"foo"}));
        assertThat(subject.getCompletionSuggestions("foo ", source), equalTo(new String[] {"abc", "def"}));
        assertThat(subject.getCompletionSuggestions("foo a", source), equalTo(new String[] {"abc"}));
        assertThat(subject.getCompletionSuggestions("foo d", source), equalTo(new String[] {"def"}));
        assertThat(subject.getCompletionSuggestions("foo g", source), is(emptyArray()));
    }
}