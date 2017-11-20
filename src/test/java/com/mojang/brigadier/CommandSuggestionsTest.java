package com.mojang.brigadier;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

        final Suggestions result = subject.getCompletionSuggestions(subject.parse("", source)).join();

        assertThat(result.getRange(), equalTo(new StringRange(0, 0)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(0, 0), "bar"),
            new Suggestion(new StringRange(0, 0), "baz"),
            new Suggestion(new StringRange(0, 0), "foo")
        )));
    }

    @Test
    public void getCompletionSuggestions_rootCommands_partial() throws Exception {
        subject.register(literal("foo"));
        subject.register(literal("bar"));
        subject.register(literal("baz"));

        final Suggestions result = subject.getCompletionSuggestions(subject.parse("b", source)).join();

        assertThat(result.getRange(), equalTo(new StringRange(1, 1)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(1, 1), "ar"),
            new Suggestion(new StringRange(1, 1), "az")
        )));
    }

    @Test
    public void getCompletionSuggestions_subCommands() throws Exception {
        subject.register(
            literal("parent")
                .then(literal("foo"))
                .then(literal("bar"))
                .then(literal("baz"))
        );

        final Suggestions result = subject.getCompletionSuggestions(subject.parse("parent ", source)).join();

        assertThat(result.getRange(), equalTo(new StringRange(7, 7)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(7, 7), "bar"),
            new Suggestion(new StringRange(7, 7), "baz"),
            new Suggestion(new StringRange(7, 7), "foo")
        )));
    }

    @Test
    public void getCompletionSuggestions_subCommands_partial() throws Exception {
        subject.register(
            literal("parent")
                .then(literal("foo"))
                .then(literal("bar"))
                .then(literal("baz"))
        );

        final ParseResults<Object> parse = subject.parse("parent b", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();

        assertThat(result.getRange(), equalTo(new StringRange(8, 8)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(8, 8), "ar"),
            new Suggestion(new StringRange(8, 8), "az")
        )));
    }

    @Test
    public void getCompletionSuggestions_redirect() throws Exception {
        final LiteralCommandNode<Object> actual = subject.register(literal("actual").then(literal("sub")));
        subject.register(literal("redirect").redirect(actual));

        final ParseResults<Object> parse = subject.parse("redirect ", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();

        assertThat(result.getRange(), equalTo(new StringRange(9, 9)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(9, 9), "sub")
        )));
    }

    @Test
    public void getCompletionSuggestions_redirectPartial() throws Exception {
        final LiteralCommandNode<Object> actual = subject.register(literal("actual").then(literal("sub")));
        subject.register(literal("redirect").redirect(actual));

        final ParseResults<Object> parse = subject.parse("redirect s", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();

        assertThat(result.getRange(), equalTo(new StringRange(10, 10)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(10, 10), "ub")
        )));
    }

    @Test
    public void getCompletionSuggestions_redirect_lots() throws Exception {
        final LiteralCommandNode<Object> loop = subject.register(literal("redirect"));
        subject.register(
            literal("redirect")
                .then(
                    literal("loop")
                        .then(
                            argument("loop", integer())
                                .redirect(loop)
                        )
                )
        );

        final Suggestions result = subject.getCompletionSuggestions(subject.parse("redirect loop 1 loop 02 loop 003 ", source)).join();

        assertThat(result.getRange(), equalTo(new StringRange(33, 33)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(33, 33), "loop")
        )));
    }

    @Test
    public void getCompletionSuggestions_execute_simulation() throws Exception {
        final LiteralCommandNode<Object> execute = subject.register(literal("execute"));
        subject.register(
            literal("execute")
                .then(
                    literal("as")
                        .then(
                            argument("name", word())
                                .redirect(execute)
                        )
                )
                .then(
                    literal("store")
                        .then(
                            argument("name", word())
                                .redirect(execute)
                        )
                )
                .then(
                    literal("run")
                        .executes(c -> 0)
                )
        );

        final ParseResults<Object> parse = subject.parse("execute as Dinnerbone as", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void getCompletionSuggestions_execute_simulation_partial() throws Exception {
        final LiteralCommandNode<Object> execute = subject.register(literal("execute"));
        subject.register(
            literal("execute")
                .then(
                    literal("as")
                        .then(literal("bar").redirect(execute))
                        .then(literal("baz").redirect(execute))
                )
                .then(
                    literal("store")
                        .then(
                            argument("name", word())
                                .redirect(execute)
                        )
                )
                .then(
                    literal("run")
                        .executes(c -> 0)
                )
        );

        final ParseResults<Object> parse = subject.parse("execute as bar as ", source);
        final Suggestions result = subject.getCompletionSuggestions(parse).join();

        assertThat(result.getRange(), equalTo(new StringRange(18, 18)));
        assertThat(result.getList(), equalTo(Lists.newArrayList(
            new Suggestion(new StringRange(18, 18), "bar"),
            new Suggestion(new StringRange(18, 18), "baz")
        )));
    }
}