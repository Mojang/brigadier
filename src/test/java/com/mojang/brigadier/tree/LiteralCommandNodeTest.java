package com.mojang.brigadier.tree;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.empty;
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
        contextBuilder = new CommandContextBuilder<>(new CommandDispatcher<>(), new Object());
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
        node.parse(reader, contextBuilder);
        assertThat(reader.getRemaining(), equalTo("bar"));
        // This should succeed, because it's the responsibility of the dispatcher to realize there's trailing text
    }

    @Test
    public void testParseInvalid() throws Exception {
        final StringReader reader = new StringReader("bar");
        try {
            node.parse(reader, contextBuilder);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(LiteralCommandNode.ERROR_INCORRECT_LITERAL));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("expected", "foo")));
        }
    }

    @Test
    public void testUsage() throws Exception {
        assertThat(node.getUsageText(), is("foo"));
    }

    @Test
    public void testSuggestions() throws Exception {
        final Set<String> set = Sets.newHashSet();
        @SuppressWarnings("unchecked") final CommandContextBuilder<Object> context = Mockito.mock(CommandContextBuilder.class);

        node.listSuggestions("", set, context);
        assertThat(set, equalTo(Sets.newHashSet("foo")));

        set.clear();
        node.listSuggestions("foo", set, context);
        assertThat(set, equalTo(Sets.newHashSet("foo")));

        set.clear();
        node.listSuggestions("food", set, context);
        assertThat(set, is(empty()));

        set.clear();
        node.listSuggestions("b", set, context);
        assertThat(set, is(empty()));
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