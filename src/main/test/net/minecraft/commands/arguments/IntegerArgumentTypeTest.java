package net.minecraft.commands.arguments;

import net.minecraft.commands.context.CommandContext;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;
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
        CommandArgumentType.CommandArgumentParseResult<Integer> result = type.parse("50");

        assertThat(result.getRaw(), is("50"));
        assertThat(result.getResult(), is(50));
    }

    @Test(expected = IllegalArgumentSyntaxException.class)
    public void testParseInvalid() throws Exception {
        type.parse("fifty");
    }

    @Test(expected = ArgumentValidationException.class)
    public void testParseTooLow() throws Exception {
        type.parse("-101");
    }

    @Test
    public void testParseLowerLimit() throws Exception {
        CommandArgumentType.CommandArgumentParseResult<Integer> result = type.parse("-100");

        assertThat(result.getRaw(), is("-100"));
        assertThat(result.getResult(), is(-100));
    }

    @Test(expected = ArgumentValidationException.class)
    public void testParseTooHigh() throws Exception {
        type.parse("101");
    }

    @Test
    public void testParseHigherLimit() throws Exception {
        CommandArgumentType.CommandArgumentParseResult<Integer> result = type.parse("100");

        assertThat(result.getRaw(), is("100"));
        assertThat(result.getResult(), is(100));
    }

    @Test
    public void testGetInteger() throws Exception {
        CommandContext context = new CommandContextBuilder().withArgument("foo", type.parse("100")).build();

        assertThat(IntegerArgumentType.getInteger(context, "foo"), is(100));
    }
}