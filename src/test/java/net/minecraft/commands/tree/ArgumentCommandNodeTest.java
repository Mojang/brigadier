package net.minecraft.commands.tree;

import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;
import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ArgumentCommandNodeTest {
    ArgumentCommandNode node;
    CommandContextBuilder contextBuilder;

    @Before
    public void setUp() throws Exception {
        node = argument("foo", integer()).build();
        contextBuilder = new CommandContextBuilder();
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("123 456", contextBuilder), is("456"));

        assertThat(contextBuilder.getArguments().containsKey("foo"), is(true));
        assertThat(contextBuilder.getArguments().get("foo").getResult(), is((Object) 123));
    }

    @Test
    public void testParseExact() throws Exception {
        assertThat(node.parse("123", contextBuilder), is(""));

        assertThat(contextBuilder.getArguments().containsKey("foo"), is(true));
        assertThat(contextBuilder.getArguments().get("foo").getResult(), is((Object) 123));
    }

    @Test(expected = IllegalArgumentSyntaxException.class)
    public void testParseInvalid() throws Exception {
        node.parse("foo", contextBuilder);
    }
}