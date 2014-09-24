package net.minecraft.commands.tree;

import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;
import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LiteralCommandNodeTest {
    LiteralCommandNode node;
    CommandContextBuilder contextBuilder;

    @Before
    public void setUp() throws Exception {
        node = literal("foo").build();
        contextBuilder = new CommandContextBuilder();
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("foo bar", contextBuilder), is("bar"));
    }

    @Test
    public void testParseExact() throws Exception {
        assertThat(node.parse("foo", contextBuilder), is(""));
    }

    @Test(expected = IllegalArgumentSyntaxException.class)
    public void testParseInvalid() throws Exception {
        node.parse("bar", contextBuilder);
    }
}