package net.minecraft.commands.arguments;

import net.minecraft.commands.exceptions.IllegalCommandArgumentException;
import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IntegerArgumentTypeTest {
    IntegerArgumentType type;

    @Before
    public void setUp() throws Exception {
        type = integer(-100, 100);
    }

    @Test
    public void testParse() throws Exception {
        assertThat(type.parse("50"), is(50));
    }

    @Test(expected = IllegalCommandArgumentException.class)
    public void testParseInvalid() throws Exception {
        type.parse("fifty");
    }

    @Test(expected = IllegalCommandArgumentException.class)
    public void testParseTooLow() throws Exception {
        type.parse("-101");
    }

    @Test
    public void testParseLowerLimit() throws Exception {
        assertThat(type.parse("-100"), is(-100));
    }

    @Test(expected = IllegalCommandArgumentException.class)
    public void testParseTooHigh() throws Exception {
        type.parse("101");
    }

    @Test
    public void testParseHigherLimit() throws Exception {
        assertThat(type.parse("100"), is(100));
    }
}