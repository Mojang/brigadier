package com.mojang.brigadier.tree;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class LiteralCommandNodeTest extends AbstractCommandNodeTest {
    LiteralCommandNode node;
    CommandContextBuilder<Object> contextBuilder;

    @Override
    protected CommandNode getCommandNode() {
        return node;
    }

    @Before
    public void setUp() throws Exception {
        node = literal("foo").build();
        contextBuilder = new CommandContextBuilder<>(new Object());
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("foo bar", contextBuilder), is("bar"));
    }

    @Test
    public void testParseExact() throws Exception {
        assertThat(node.parse("foo", contextBuilder), is(""));
    }

    @Test
    public void testParseSimilar() throws Exception {
        try {
            node.parse("foobar", contextBuilder);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(LiteralCommandNode.ERROR_INCORRECT_LITERAL));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("expected", "foo")));
        }
    }

    @Test
    public void testParseInvalid() throws Exception {
        try {
            node.parse("bar", contextBuilder);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(LiteralCommandNode.ERROR_INCORRECT_LITERAL));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("expected", "foo")));
        }
    }

    @Test
    public void testUsage() throws Exception {
        assertThat(node.getUsageText(), is("foo"));
    }

    @Test
    public void testEquals() throws Exception {
        Command command = mock(Command.class);

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
}