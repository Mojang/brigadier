package com.mojang.brigadier.arguments;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.mojang.brigadier.arguments.ByteArgumentType.byteArg;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ByteArgumentTypeTest {
    private ByteArgumentType type;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        type = byteArg((byte) -100, (byte) 100);
    }

    @Test
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        assertThat(byteArg().parse(reader), is((byte) 15));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            byteArg((byte) 0, (byte) 100).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.byteTooLow()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            byteArg((byte) -100, (byte) 0).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.byteTooHigh()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
                .addEqualityGroup(byteArg(), byteArg())
                .addEqualityGroup(byteArg((byte) -100, (byte) 100), byteArg((byte) -100, (byte) 100))
                .addEqualityGroup(byteArg((byte) -100, (byte) 50), byteArg((byte) -100, (byte) 50))
                .addEqualityGroup(byteArg((byte) -50, (byte) 100), byteArg((byte) -50, (byte) 100))
                .testEquals();
    }

    @Test
    public void testToString() throws Exception {
        assertThat(byteArg(), hasToString("byteArg()"));
        assertThat(byteArg((byte) -100), hasToString("byteArg(-100)"));
        assertThat(byteArg((byte) -100, (byte) 100), hasToString("byteArg(-100, 100)"));
        assertThat(byteArg(Byte.MIN_VALUE, (byte) 100), hasToString("byteArg(-128, 100)"));
    }
}
