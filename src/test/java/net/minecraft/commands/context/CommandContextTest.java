package net.minecraft.commands.context;

import net.minecraft.commands.arguments.IntegerArgumentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CommandContextTest {
    CommandContextBuilder<Object> builder;
    @Mock Object source;

    @Before
    public void setUp() throws Exception {
        builder = new CommandContextBuilder<Object>(source);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_nonexistent() throws Exception {
        builder.build().getArgument("foo", Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgument_wrongType() throws Exception {
        CommandContext<Object> context = builder.withArgument("foo", IntegerArgumentType.integer().parse("123")).build();
        context.getArgument("foo", String.class);
    }

    @Test
    public void testGetArgument() throws Exception {
        CommandContext<Object> context = builder.withArgument("foo", IntegerArgumentType.integer().parse("123")).build();
        assertThat(context.getArgument("foo", int.class).getResult(), is(123));
    }

    @Test
    public void testSource() throws Exception {
        assertThat(builder.build().getSource(), is(source));
    }
}