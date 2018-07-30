// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.google.common.collect.Lists;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.junit.Before;
import org.junit.Test;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class LiteralCommandNodeTest extends AbstractCommandNodeTest {
    private LiteralCommandNode<Object> node;
    private CommandContextBuilder<Object> contextBuilder;

    @Override
    protected CommandNode<Object> getCommandNode() {
        return node;
    }

    @Before
    public void setUp() throws Exception {
        node = literal("foo").build();
        contextBuilder = new CommandContextBuilder<>(new CommandDispatcher<>(), new Object(), new RootCommandNode<>(), 0);
    }

    @Test
    public void testParse() throws Exception {
        final StringReader reader = new StringReader("foo bar");
        node.parse(reader, contextBuilder);
        assertThat(reader.getRemaining(), equalTo(" bar"));
    }

    @Test
    public void testParseExact() throws Exception {
        final StringReader reader = new StringReader("foo");
        node.parse(reader, contextBuilder);
        assertThat(reader.getRemaining(), equalTo(""));
    }

    @Test
    public void testParseSimilar() throws Exception {
        final StringReader reader = new StringReader("foobar");
        try {
            node.parse(reader, contextBuilder);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testParseInvalid() throws Exception {
        final StringReader reader = new StringReader("bar");
        try {
            node.parse(reader, contextBuilder);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testUsage() throws Exception {
        assertThat(node.getUsageText(), is("foo"));
    }

    @Test
    public void testSuggestions() throws Exception {
        final Suggestions empty = node.listSuggestions(contextBuilder.build(""), new SuggestionsBuilder("", 0)).join();
        assertThat(empty.getList(), equalTo(Lists.newArrayList(new Suggestion(StringRange.at(0), "foo"))));

        final Suggestions foo = node.listSuggestions(contextBuilder.build("foo"), new SuggestionsBuilder("foo", 0)).join();
        assertThat(foo.isEmpty(), is(true));

        final Suggestions food = node.listSuggestions(contextBuilder.build("food"), new SuggestionsBuilder("food", 0)).join();
        assertThat(food.isEmpty(), is(true));

        final Suggestions b = node.listSuggestions(contextBuilder.build("b"), new SuggestionsBuilder("b", 0)).join();
        assertThat(food.isEmpty(), is(true));
    }

    @Test
    public void testEquals() throws Exception {
        @SuppressWarnings("unchecked") final Command<Object> command = mock(Command.class);

        new EqualsTester()
            .addEqualityGroup(
                literal("foo").build(),
                literal("foo").build()
            )
            .addEqualityGroup(
                literal("bar").executes(command).build(),
                literal("bar").executes(command).build()
            )
            .addEqualityGroup(
                literal("bar").build(),
                literal("bar").build()
            )
            .addEqualityGroup(
                literal("foo").then(
                    literal("bar")
                ).build(),
                literal("foo").then(
                    literal("bar")
                ).build()
            )
            .testEquals();
    }

    @Test
    public void testCreateBuilder() throws Exception {
        final LiteralArgumentBuilder<Object> builder = node.createBuilder();
        assertThat(builder.getLiteral(), is(node.getLiteral()));
        assertThat(builder.getRequirement(), is(node.getRequirement()));
        assertThat(builder.getCommand(), is(node.getCommand()));
    }
}