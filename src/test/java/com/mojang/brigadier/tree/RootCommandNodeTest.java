package com.mojang.brigadier.tree;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.context.CommandContextBuilder;
import org.junit.Before;
import org.junit.Test;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RootCommandNodeTest extends AbstractCommandNodeTest {
    RootCommandNode node;

    @Override
    protected CommandNode getCommandNode() {
        return node;
    }

    @Before
    public void setUp() throws Exception {
        node = new RootCommandNode();
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("foo bar baz", new CommandContextBuilder<Object>(new Object())), is("foo bar baz"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddChildNoRoot() throws Exception {
        node.addChild(new RootCommandNode());
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new RootCommandNode(),
                new RootCommandNode()
            )
            .addEqualityGroup(
                new RootCommandNode() {{
                    addChild(literal("foo").build());
                }},
                new RootCommandNode() {{
                    addChild(literal("foo").build());
                }}
            )
            .testEquals();
    }
}