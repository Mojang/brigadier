package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LiteralArgumentBuilderTest {
    LiteralArgumentBuilder builder;
    @Mock
    Command command;

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
        LiteralCommandNode node = builder.executes(command).build();

        assertThat(node.getLiteral(), is("foo"));
        assertThat(node.getCommand(), is(command));
    }

    @Test
    public void testBuildWithChildren() throws Exception {
        builder.then(argument("bar", integer()));
        builder.then(argument("baz", integer()));
        LiteralCommandNode node = builder.build();

        assertThat(node.getChildren(), hasSize(2));
    }
}