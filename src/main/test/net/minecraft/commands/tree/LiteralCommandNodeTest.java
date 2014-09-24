package net.minecraft.commands.tree;

import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;
import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.builder.LiteralArgumentBuilder.literal;
import static net.minecraft.commands.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LiteralCommandNodeTest {
    LiteralCommandNode node;

    @Before
    public void setUp() throws Exception {
        node = literal("foo").build();
    }

    @Test
    public void testParse() throws Exception {
        assertThat((LiteralCommandNode) node.parse("foo"), is(node));
    }

    @Test(expected = IllegalArgumentSyntaxException.class)
    public void testParseInvalid() throws Exception {
        node.parse("bar");
    }

    @Test
    public void testParseChild() throws Exception {
        CommandNode child = argument("bar", integer()).build();

        node.addChild(child);

        assertThat(node.parse("foo 123"), is(child));
    }

    @Test(expected = IllegalArgumentSyntaxException.class)
    public void testParseInvalidChild() throws Exception {
        node.addChild(argument("bar", integer()).build());

        node.parse("foo bar");
    }

    @Test(expected = IllegalArgumentSyntaxException.class)
    public void testParseNoChildren() throws Exception {
        node.parse("foo 123");
    }
}