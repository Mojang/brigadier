package net.minecraft.commands.builder;

import net.minecraft.commands.tree.LiteralCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LiteralArgumentBuilderTest {
    LiteralArgumentBuilder builder;
    @Mock
    Runnable executor;

    @Before
    public void setUp() throws Exception {
        builder = new LiteralArgumentBuilder("foo");
    }

    @Test
    public void testBuild() throws Exception {
        LiteralCommandNode node = builder.build();

        assertThat(node.getLiteral(), is("foo"));
    }

    @Test
    public void testBuildWithExecutor() throws Exception {
        LiteralCommandNode node = builder.executes(executor).build();

        assertThat(node.getLiteral(), is("foo"));
        assertThat(node.getExecutor(), is(executor));
    }

    @Test
    public void testBuildWithChildren() throws Exception {
        builder.then(argument("bar", integer()));
        builder.then(argument("baz", integer()));
        LiteralCommandNode node = builder.build();

        assertThat(node.getChildren(), hasSize(2));
    }
}