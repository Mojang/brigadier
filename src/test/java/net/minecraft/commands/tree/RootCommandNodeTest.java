package net.minecraft.commands.tree;

import com.google.common.testing.EqualsTester;
import net.minecraft.commands.context.CommandContextBuilder;
import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.builder.LiteralArgumentBuilder.literal;
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