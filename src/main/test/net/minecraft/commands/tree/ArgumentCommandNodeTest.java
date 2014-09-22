package net.minecraft.commands.tree;

import net.minecraft.commands.exceptions.IllegalCommandArgumentException;
import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ArgumentCommandNodeTest {
    ArgumentCommandNode node;

    @Before
    public void setUp() throws Exception {
        node = argument("foo", integer()).build();
    }

    @Test
    public void testParse() throws Exception {
        assertThat((ArgumentCommandNode) node.parse("123"), is(node));
    }

    @Test(expected = IllegalCommandArgumentException.class)
    public void testParseInvalid() throws Exception {
        node.parse("bar");
    }

    @Test
    public void testParseChild() throws Exception {
        CommandNode child = argument("bar", integer()).build();

        node.addChild(child);

        assertThat(node.parse("123 123"), is(child));
    }

    @Test(expected = IllegalCommandArgumentException.class)
    public void testParseInvalidChild() throws Exception {
        node.addChild(argument("bar", integer()).build());

        node.parse("123 bar");
    }

    @Test(expected = IllegalCommandArgumentException.class)
    public void testParseNoChildren() throws Exception {
        node.parse("123 123");
    }
}