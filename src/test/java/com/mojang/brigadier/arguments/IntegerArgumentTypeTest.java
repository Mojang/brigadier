package com.mojang.brigadier.arguments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class IntegerArgumentTypeTest {
    private IntegerArgumentType type;
    @Mock
    private Object source;
    @Mock
    private CommandDispatcher<Object> dispatcher;

    @Before
    public void setUp() throws Exception {
        type = integer(-100, 100);
    }

    @Test
    public void testParse() throws Exception {
        ParsedArgument<Object, Integer> result = type.parse("50", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("50"));
        assertThat(result.getResult(source), is(50));
    }

    @Test
    public void testParse_suffix() throws Exception {
        ParsedArgument<Object, Integer> result = integer(0, 100, "L").parse("50L", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("50L"));
        assertThat(result.getResult(source), is(50));
    }

    @Test
    public void testParse_noSuffix() throws Exception {
        try {
            integer(0, 0, "L").parse("50", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_WRONG_SUFFIX));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("suffix", "L")));
        }
    }

    @Test
    public void testParse_wrongSuffix() throws Exception {
        try {
            integer(0, 0, "L").parse("50B", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_WRONG_SUFFIX));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("suffix", "L")));
        }
    }

    @Test
    public void testParse_unexpectedSuffix() throws Exception {
        try {
            type.parse("50L", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_NOT_A_NUMBER));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", "50L")));
        }
    }

    @Test
    public void testParseInvalid() throws Exception {
        try {
            type.parse("fifty", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_NOT_A_NUMBER));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", "fifty")));
        }
    }

    @Test
    public void testParseTooLow() throws Exception {
        try {
            type.parse("-101", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_TOO_SMALL));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", -101, "minimum", -100)));
        }
    }

    @Test
    public void testParseLowerLimit() throws Exception {
        ParsedArgument<Object, Integer> result = type.parse("-100", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("-100"));
        assertThat(result.getResult(source), is(-100));
    }

    @Test
    public void testParseTooHigh() throws Exception {
        try {
            type.parse("101", new CommandContextBuilder<>(dispatcher, source));
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_TOO_BIG));
            assertThat(ex.getData(), is(ImmutableMap.<String, Object>of("found", 101, "maximum", 100)));
        }
    }

    @Test
    public void testParseHigherLimit() throws Exception {
        ParsedArgument<Object, Integer> result = type.parse("100", new CommandContextBuilder<>(dispatcher, source));

        assertThat(result.getRaw(), is("100"));
        assertThat(result.getResult(source), is(100));
    }

    @Test
    public void testGetInteger() throws Exception {
        CommandContext context = new CommandContextBuilder<>(dispatcher, new Object()).withArgument("foo", type.parse("100", new CommandContextBuilder<>(dispatcher, source))).build();

        assertThat(IntegerArgumentType.getInteger(context, "foo"), is(100));
    }

    @Test
    public void testSuggestions() throws Exception {
        Set<String> set = Sets.newHashSet();
        @SuppressWarnings("unchecked") final CommandContextBuilder<Object> context = Mockito.mock(CommandContextBuilder.class);
        type.listSuggestions("", set, context);
        assertThat(set, is(empty()));
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(integer(), integer())
            .addEqualityGroup(integer(-100, 100), integer(-100, 100))
            .addEqualityGroup(integer(-100, 50), integer(-100, 50))
            .addEqualityGroup(integer(-50, 100), integer(-50, 100))
            .addEqualityGroup(integer(-50, 100, "foo"), integer(-50, 100, "foo"))
            .addEqualityGroup(integer(-50, 100, "bar"), integer(-50, 100, "bar"))
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