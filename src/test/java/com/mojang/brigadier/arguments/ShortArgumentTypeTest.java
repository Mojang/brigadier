package com.mojang.brigadier.arguments;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.mojang.brigadier.arguments.ShortArgumentType.shortArg;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ShortArgumentTypeTest {
    private ShortArgumentType type;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        type = shortArg((short) -100, (short) 100);
    }

    @Test
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        assertThat(shortArg().parse(reader), is((short) 15));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            shortArg((short) 0, (short) 100).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.shortTooLow()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            shortArg((short) -100, (short) 0).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.shortTooHigh()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
                .addEqualityGroup(shortArg(), shortArg())
                .addEqualityGroup(shortArg((short) -100, (short) 100), shortArg((short) -100, (short) 100))
                .addEqualityGroup(shortArg((short) -100, (short) 50), shortArg((short) -100, (short) 50))
                .addEqualityGroup(shortArg((short) -50, (short) 100), shortArg((short) -50, (short) 100))
                .testEquals();
    }

    @Test
    public void testToString() throws Exception {
        assertThat(shortArg(), hasToString("shortArg()"));
        assertThat(shortArg((short) -100), hasToString("shortArg(-100)"));
        assertThat(shortArg((short) -100, (short) 100), hasToString("shortArg(-100, 100)"));
        assertThat(shortArg(Short.MIN_VALUE, (short) 100), hasToString("shortArg(-32768, 100)"));
    }
}
