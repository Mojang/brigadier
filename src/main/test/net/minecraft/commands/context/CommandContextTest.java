package net.minecraft.commands.context;

import net.minecraft.commands.arguments.IntegerArgumentType;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CommandContextTest {
    CommandContext context;

    @Before
    public void setUp() throws Exception {
        context = new CommandContextBuilder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_nonexistent() throws Exception {
        context.getArgument("foo", Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_wrongType() throws Exception {
        context = new CommandContextBuilder().withArgument("foo", IntegerArgumentType.integer().parse("123")).build();
        context.getArgument("foo", String.class);
    }

    @Test
    public void testGetArgument() throws Exception {
        context = new CommandContextBuilder().withArgument("foo", IntegerArgumentType.integer().parse("123")).build();
        assertThat(context.getArgument("foo", int.class).getResult(), is(123));
    }
}