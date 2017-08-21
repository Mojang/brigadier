package com.mojang.brigadier.arguments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class IntegerArgumentTypeTest {
    private IntegerArgumentType type;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        type = integer(-100, 100);
    }

    @Test
    public void parse_noSuffix() throws Exception {
        final StringReader reader = new StringReader("15");
        assertThat(integer().parse(reader, context), is(15));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_suffix() throws Exception {
        final StringReader reader = new StringReader("15L");
        assertThat(integer(0, 100, "L").parse(reader, context), is(15));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_suffix_incorrect() throws Exception {
        final StringReader reader = new StringReader("15W");
        try {
            integer(0, 100, "L").parse(reader, context);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_WRONG_SUFFIX));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("suffix", "L")));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_suffix_missing() throws Exception {
        final StringReader reader = new StringReader("15");
        try {
            integer(0, 100, "L").parse(reader, context);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_WRONG_SUFFIX));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("suffix", "L")));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            integer(0, 100).parse(reader, context);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_TOO_SMALL));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("found", "-5", "minimum", "0")));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            integer(-100, 0).parse(reader, context);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(IntegerArgumentType.ERROR_TOO_BIG));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("found", "5", "maximum", "0")));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testSuggestions() throws Exception {
        final Set<String> set = Sets.newHashSet();
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

    @Test
    public void testUsageSuffix() throws Exception {
        assertThat(integer().getUsageSuffix(), equalTo(null));
    }

    @Test
    public void testUsageSuffix_suffix() throws Exception {
        assertThat(integer(0, 100, "L").getUsageSuffix(), equalTo("L"));
    }

    @Test
    public void testUsageText() throws Exception {
        assertThat(integer().getUsageText(), equalTo("int"));
    }

    @Test
    public void testUsageText_suffix() throws Exception {
        assertThat(integer(0, 100, "L").getUsageText(), equalTo("int"));
    }
}