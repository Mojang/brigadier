package com.mojang.brigadier;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CommandSuggestionsTest {
    private CommandDispatcher<Object> subject;
    @Mock
    private Object source;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher<>();
    }

    @Test
    public void getCompletionSuggestions_rootCommands() throws Exception {
        subject.register(literal("foo"));
        subject.register(literal("bar"));
        subject.register(literal("baz"));

        final CommandSuggestions result = subject.getCompletionSuggestions(subject.parse("", source));

        assertThat(result.getRange(), equalTo(new StringRange(0, 0)));
        assertThat(result.getSuggestions(), equalTo(Lists.newArrayList("bar", "baz", "foo")));
    }

    @Test
    public void getCompletionSuggestions_rootCommands_partial() throws Exception {
        subject.register(literal("foo"));
        subject.register(literal("bar"));
        subject.register(literal("baz"));

        final CommandSuggestions result = subject.getCompletionSuggestions(subject.parse("b", source));

        assertThat(result.getRange(), equalTo(new StringRange(0, 1)));
        assertThat(result.getSuggestions(), equalTo(Lists.newArrayList("bar", "baz")));
    }

    @Test
    public void getCompletionSuggestions_subCommands() throws Exception {
        subject.register(
            literal("parent")
                .then(literal("foo"))
                .then(literal("bar"))
                .then(literal("baz"))
        );

        final CommandSuggestions result = subject.getCompletionSuggestions(subject.parse("parent ", source));

        assertThat(result.getRange(), equalTo(new StringRange(7, 7)));
        assertThat(result.getSuggestions(), equalTo(Lists.newArrayList("bar", "baz", "foo")));
    }

    @Test
    public void getCompletionSuggestions_subCommands_partial() throws Exception {
        subject.register(
            literal("parent")
                .then(literal("foo"))
                .then(literal("bar"))
                .then(literal("baz"))
        );

        final CommandSuggestions result = subject.getCompletionSuggestions(subject.parse("parent b", source));

        assertThat(result.getRange(), equalTo(new StringRange(7, 8)));
        assertThat(result.getSuggestions(), equalTo(Lists.newArrayList("bar", "baz")));
    }
}