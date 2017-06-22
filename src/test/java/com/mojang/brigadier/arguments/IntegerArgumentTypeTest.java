package com.mojang.brigadier.arguments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class IntegerArgumentTypeTest {
    private IntegerArgumentType type;

    @Before
    public void setUp() throws Exception {
        type = integer(-100, 100);
    }

    @Test
    public void testParse() throws Exception {
        ParsedArgument<Integer> result = type.parse("50");

        assertThat(result.getRaw(), is("50"));
        assertThat(result.getResult(), is(50));
    }

    @Test
    public void testParseInvalid() throws Exception {
        try {
            type.parse("fifty");
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_NOT_A_NUMBER));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", "fifty")));
        }
    }

    @Test
    public void testParseTooLow() throws Exception {
        try {
            type.parse("-101");
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_TOO_SMALL));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", -101, "minimum", -100)));
        }
    }

    @Test
    public void testParseLowerLimit() throws Exception {
        ParsedArgument<Integer> result = type.parse("-100");

        assertThat(result.getRaw(), is("-100"));
        assertThat(result.getResult(), is(-100));
    }

    @Test
    public void testParseTooHigh() throws Exception {
        try {
            type.parse("101");
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_TOO_BIG));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", 101, "maximum", 100)));
        }
    }

    @Test
    public void testParseHigherLimit() throws Exception {
        ParsedArgument<Integer> result = type.parse("100");

        assertThat(result.getRaw(), is("100"));
        assertThat(result.getResult(), is(100));
    }

    @Test
    public void testGetInteger() throws Exception {
        CommandContext context = new CommandContextBuilder<>(new Object()).withArgument("foo", type.parse("100")).build();

        assertThat(IntegerArgumentType.getInteger(context, "foo"), is(100));
    }

    @Test
    public void testSuggestions() throws Exception {
        Set<String> set = Sets.newHashSet();
        type.listSuggestions("", set);
        assertThat(set, is(empty()));
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(integer(), integer())
            .addEqualityGroup(integer(-100, 100), integer(-100, 100))
            .addEqualityGroup(integer(-100, 50), integer(-100, 50))
            .addEqualityGroup(integer(-50, 100), integer(-50, 100))
            .testEquals();
    }

    @Test
    public void testToString() throws Exception {
        assertThat(integer(), hasToString("integer()"));
        assertThat(integer(-100), hasToString("integer(-100)"));
        assertThat(integer(-100, 100), hasToString("integer(-100, 100)"));
        assertThat(integer(Integer.MIN_VALUE, 100), hasToString("integer(-2147483648, 100)"));
    }
}