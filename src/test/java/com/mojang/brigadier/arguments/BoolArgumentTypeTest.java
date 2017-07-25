package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.mojang.brigadier.arguments.BoolArgumentType.ERROR_INVALID;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BoolArgumentTypeTest {
    private BoolArgumentType type;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        type = bool();
    }

    @Test
    public void parse() throws Exception {
        StringReader reader = mock(StringReader.class);
        when(reader.readBoolean()).thenReturn(true);
        assertThat(type.parse(reader, context), is(true));
        verify(reader).readBoolean();
    }

    @Test
    public void usageText() throws Exception {
        assertThat(type.getUsageText(), equalTo("bool"));
    }
}