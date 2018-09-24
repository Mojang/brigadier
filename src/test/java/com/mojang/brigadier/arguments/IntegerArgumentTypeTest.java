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

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
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
    public void parse() throws Exception {
        final StringReader reader = new StringReader("15");
        assertThat(integer().parse(reader), is(15));
        assertThat(reader.canRead(), is(false));
    }

    @Test
    public void parse_tooSmall() throws Exception {
        final StringReader reader = new StringReader("-5");
        try {
            integer(0, 100).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow()));
            assertThat(ex.getCursor(), is(0));
        }
    }

    @Test
    public void parse_tooBig() throws Exception {
        final StringReader reader = new StringReader("5");
        try {
            integer(-100, 0).parse(reader);
            fail();
        } catch (final CommandSyntaxException ex) {
            assertThat(ex.getType(), is(CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh()));
            assertThat(ex.getCursor(), is(0));
        }
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