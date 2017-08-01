package com.mojang.brigadier.arguments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class FloatArgumentTypeTest {
    private FloatArgumentType type;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        type = floatArg(-100, 100);
    }

    @Test
    public void parse_noSuffix() throws Exception {
        final StringReader reader = new StringReader("15");
        assertThat(floatArg().parse(reader, context), is(15f));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_suffix() throws Exception {
        final StringReader reader = new StringReader("15L");
        assertThat(floatArg(0, 100, "L").parse(reader, context), is(15f));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_suffix_incorrect() throws Exception {
        final StringReader reader = new StringReader("15W");
        try {
            floatArg(0, 100, "L").parse(reader, context);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(FloatArgumentType.ERROR_WRONG_SUFFIX));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("suffix", "L")));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_suffix_missing() throws Exception {
        final StringReader reader = new StringReader("15");
        try {
            floatArg(0, 100, "L").parse(reader, context);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(FloatArgumentType.ERROR_WRONG_SUFFIX));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("suffix", "L")));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            floatArg(0, 100).parse(reader, context);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(FloatArgumentType.ERROR_TOO_SMALL));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("found", "-5.0", "minimum", "0.0")));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            floatArg(-100, 0).parse(reader, context);
            fail();
        } catch (final CommandException ex) {
            assertThat(ex.getType(), is(FloatArgumentType.ERROR_TOO_BIG));
            assertThat(ex.getData(), equalTo(ImmutableMap.<String, Object>of("found", "5.0", "maximum", "0.0")));
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
            .addEqualityGroup(floatArg(), floatArg())
            .addEqualityGroup(floatArg(-100, 100), floatArg(-100, 100))
            .addEqualityGroup(floatArg(-100, 50), floatArg(-100, 50))
            .addEqualityGroup(floatArg(-50, 100), floatArg(-50, 100))
            .addEqualityGroup(floatArg(-50, 100, "foo"), floatArg(-50, 100, "foo"))
            .addEqualityGroup(floatArg(-50, 100, "bar"), floatArg(-50, 100, "bar"))
            .testEquals();
    }

    @Test
    public void testToString() throws Exception {
        assertThat(floatArg(), hasToString("float()"));
        assertThat(floatArg(-100), hasToString("float(-100.0)"));
        assertThat(floatArg(-100, 100), hasToString("float(-100.0, 100.0)"));
        assertThat(floatArg(Integer.MIN_VALUE, 100), hasToString("float(-2.14748365E9, 100.0)"));
    }

    @Test
    public void testUsageSuffix() throws Exception {
        assertThat(floatArg().getUsageSuffix(), equalTo(null));
    }

    @Test
    public void testUsageSuffix_suffix() throws Exception {
        assertThat(floatArg(0, 100, "L").getUsageSuffix(), equalTo("L"));
    }

    @Test
    public void testUsageText() throws Exception {
        assertThat(floatArg().getUsageText(), equalTo("float"));
    }

    @Test
    public void testUsageText_suffix() throws Exception {
        assertThat(floatArg(0, 100, "L").getUsageText(), equalTo("float"));
    }
}