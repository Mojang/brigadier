package net.minecraft.commands.tree;

import net.minecraft.commands.context.CommandContextBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RootCommandNodeTest {
    RootCommandNode node;

    @Before
    public void setUp() throws Exception {
        node = new RootCommandNode();
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("foo bar baz", new CommandContextBuilder()), is("foo bar baz"));

    }
}