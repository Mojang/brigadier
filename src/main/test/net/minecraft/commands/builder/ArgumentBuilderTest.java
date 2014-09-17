package net.minecraft.commands.builder;

import org.junit.Before;
import org.junit.Test;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class ArgumentBuilderTest {
    ArgumentBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new ArgumentBuilder() {};
    }

    @Test
    public void testArguments() throws Exception {
        RequiredArgumentBuilder argument = argument("bar", integer());

        builder.then(argument);

        assertThat(builder.getArguments(), hasSize(1));
        assertThat(builder.getArguments(), hasItems((ArgumentBuilder) argument));
    }
}