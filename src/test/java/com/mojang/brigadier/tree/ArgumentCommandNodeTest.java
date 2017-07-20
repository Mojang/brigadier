package com.mojang.brigadier.tree;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ArgumentCommandNodeTest extends AbstractCommandNodeTest {
    private ArgumentCommandNode<Object, Integer> node;
    private CommandContextBuilder<Object> contextBuilder;
    private Object source = new Object();

    @Override
    protected CommandNode<Object> getCommandNode() {
        return node;
    }

    @Before
    public void setUp() throws Exception {
        node = argument("foo", integer()).build();
        contextBuilder = new CommandContextBuilder<>(new CommandDispatcher<>(), new Object());
    }

    @Test
    public void testParse() throws Exception {
        assertThat(node.parse("123 456", contextBuilder), is(" 456"));

        assertThat(contextBuilder.getArguments().containsKey("foo"), is(true));
        assertThat(contextBuilder.getArguments().get("foo").getResult(source), is(123));
    }

    @Test
    public void testParseExact() throws Exception {
        assertThat(node.parse("123", contextBuilder), is(""));

        assertThat(contextBuilder.getArguments().containsKey("foo"), is(true));
        assertThat(contextBuilder.getArguments().get("foo").getResult(source), is(123));
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
        assertThat(node.getUsageText(), is("<foo: int>"));
    }

    @Test
    public void testUsage_suffix() throws Exception {
        node = argument("foo", integer(0, 100, "L")).build();
        assertThat(node.getUsageText(), is("<foo: int>L"));
    }

    @Test
    public void testUsage_empty() throws Exception {
        ArgumentCommandNode<Object, String> node = argument("foo", StringArgumentType.word()).build();
        assertThat(node.getUsageText(), is("<foo>"));
    }

    @Test
    public void testSuggestions() throws Exception {
        Set<String> set = Sets.newHashSet();
        @SuppressWarnings("unchecked") final CommandContextBuilder<Object> context = Mockito.mock(CommandContextBuilder.class);
        node.listSuggestions("", set, context);
        assertThat(set, is(empty()));
    }

    @Test
    public void testEquals() throws Exception {
        @SuppressWarnings("unchecked") Command<Object> command = (Command<Object>) mock(Command.class);

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

    @Test
    public void testCreateBuilder() throws Exception {
        final RequiredArgumentBuilder<Object, Integer> builder = node.createBuilder();
        assertThat(builder.getName(), is(node.getName()));
        assertThat(builder.getType(), is(node.getType()));
        assertThat(builder.getRequirement(), is(node.getRequirement()));
        assertThat(builder.getCommand(), is(node.getCommand()));
    }
}