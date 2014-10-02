package com.mojang.brigadier.tree;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Before;
import org.junit.Test;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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
        contextBuilder = new CommandContextBuilder<Object>(new Object());
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("foo bar", contextBuilder), is("bar"));
    }

    @Test
    public void testParseExact() throws Exception {
        assertThat(node.parse("foo", contextBuilder), is(""));
    }

    @Test(expected = CommandException.class)
    public void testParseSimilar() throws Exception {
        node.parse("foobar", contextBuilder);
    }

    @Test(expected = CommandException.class)
    public void testParseInvalid() throws Exception {
        node.parse("bar", contextBuilder);
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