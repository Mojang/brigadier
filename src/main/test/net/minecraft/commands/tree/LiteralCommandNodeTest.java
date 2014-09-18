package net.minecraft.commands.tree;

import net.minecraft.commands.exceptions.IllegalCommandArgumentException;
import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.builder.CommandBuilder.command;

public class LiteralCommandNodeTest {
    LiteralCommandNode node;

    @Before
    public void setUp() throws Exception {
        node = command("foo").build();
    }

    @Test
    public void testParse() throws Exception {
        node.parse("foo");
    }

    @Test(expected = IllegalCommandArgumentException.class)
    public void testParseInvalid() throws Exception {
        node.parse("bar");
    }
}