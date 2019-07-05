package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static com.mojang.brigadier.arguments.UuidArgumentType.uuid;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UuidArgumentTypeTest {
    private UuidArgumentType type;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        type = uuid();
    }

    @Test
    public void parse() throws Exception {
        final StringReader reader = new StringReader("00000000-0000-0000-0000-000000000000");
        assertThat(uuid().parse(reader), is(new UUID(0L, 0L)));
    }
}
