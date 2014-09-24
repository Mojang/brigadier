package net.minecraft.commands.builder;

import net.minecraft.commands.Command;
import net.minecraft.commands.arguments.CommandArgumentType;
import net.minecraft.commands.tree.ArgumentCommandNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RequiredArgumentBuilderTest {
    @Mock CommandArgumentType<Integer> type;
    RequiredArgumentBuilder<Integer> builder;
    @Mock
    Command command;

    @Before
    public void setUp() throws Exception {
        builder = argument("foo", type);
    }

    @Test
    public void testBuild() throws Exception {
        ArgumentCommandNode<Integer> node = builder.build();

        assertThat(node.getName(), is("foo"));
        assertThat(node.getType(), is(type));
    }

    @Test
    public void testBuildWithExecutor() throws Exception {
        ArgumentCommandNode<Integer> node = builder.executes(command).build();

        assertThat(node.getName(), is("foo"));
        assertThat(node.getType(), is(type));
        assertThat(node.getCommand(), is(command));
    }

    @Test
    public void testBuildWithChildren() throws Exception {
        builder.then(argument("bar", integer()));
        builder.then(argument("baz", integer()));
        ArgumentCommandNode node = builder.build();

        assertThat(node.getChildren(), hasSize(2));
    }
}