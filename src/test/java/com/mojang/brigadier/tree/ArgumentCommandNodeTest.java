package com.mojang.brigadier.tree;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ArgumentCommandNodeTest extends AbstractCommandNodeTest {
    ArgumentCommandNode<Integer> node;
    CommandContextBuilder<Object> contextBuilder;

    @Override
    protected CommandNode getCommandNode() {
        return node;
    }

    @Before
    public void setUp() throws Exception {
        node = argument("foo", integer()).build();
        contextBuilder = new CommandContextBuilder<>(new Object());
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("123 456", contextBuilder), is("456"));

        assertThat(contextBuilder.getArguments().containsKey("foo"), is(true));
        assertThat(contextBuilder.getArguments().get("foo").getResult(), is(123));
    }

    @Test
    public void testParseExact() throws Exception {
        assertThat(node.parse("123", contextBuilder), is(""));

        assertThat(contextBuilder.getArguments().containsKey("foo"), is(true));
        assertThat(contextBuilder.getArguments().get("foo").getResult(), is(123));
    }

    @Test
    public void testParseInvalid() throws Exception {
        try {
            node.parse("foo", contextBuilder);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_NOT_A_NUMBER));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", "foo")));
        }
    }

    @Test
    public void testUsage() throws Exception {
        assertThat(node.getUsageText(), is("<foo>"));
    }

    @Test
    public void testEquals() throws Exception {
        Command command = mock(Command.class);

        new EqualsTester()
            .addEqualityGroup(
                argument("foo", integer()).build(),
                argument("foo", integer()).build()
            )
            .addEqualityGroup(
                argument("foo", integer()).executes(command).build(),
                argument("foo", integer()).executes(command).build()
            )
            .addEqualityGroup(
                argument("bar", integer(-100, 100)).build(),
                argument("bar", integer(-100, 100)).build()
            )
            .addEqualityGroup(
                argument("foo", integer(-100, 100)).build(),
                argument("foo", integer(-100, 100)).build()
            )
            .addEqualityGroup(
                argument("foo", integer()).then(
                    argument("bar", integer())
                ).build(),
                argument("foo", integer()).then(
                    argument("bar", integer())
                ).build()
            )
            .testEquals();
    }
}