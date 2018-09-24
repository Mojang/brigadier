// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.arguments;

import com.google.common.testing.EqualsTester;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
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
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        assertThat(floatArg().parse(reader), is(15f));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            floatArg(0, 100).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooLow()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            floatArg(-100, 0).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooHigh()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(floatArg(), floatArg())
            .addEqualityGroup(floatArg(-100, 100), floatArg(-100, 100))
            .addEqualityGroup(floatArg(-100, 50), floatArg(-100, 50))
            .addEqualityGroup(floatArg(-50, 100), floatArg(-50, 100))
            .testEquals();
    }

    @Test
    public void testToString() throws Exception {
        assertThat(floatArg(), hasToString("float()"));
        assertThat(floatArg(-100), hasToString("float(-100.0)"));
        assertThat(floatArg(-100, 100), hasToString("float(-100.0, 100.0)"));
        assertThat(floatArg(Integer.MIN_VALUE, 100), hasToString("float(-2.14748365E9, 100.0)"));
    }
}