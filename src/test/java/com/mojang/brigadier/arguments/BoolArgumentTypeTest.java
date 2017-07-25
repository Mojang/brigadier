package com.mojang.brigadier.arguments;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.mojang.brigadier.arguments.BoolArgumentType.ERROR_INVALID;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BoolArgumentTypeTest {
    private BoolArgumentType type;
    @Mock
    private Object source;
    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() throws Exception {
        type = bool();
    }

    @Test
    public void parse_true() throws Exception {
        ParsedArgument<Object, Boolean> parse = type.parse("true", context);
        assertThat(parse.getResult(), is(true));
        assertThat(parse.getRaw(), equalTo("true"));
    }

    @Test
    public void parse_false() throws Exception {
        ParsedArgument<Object, Boolean> parse = type.parse("false", context);
        assertThat(parse.getResult(), is(false));
        assertThat(parse.getRaw(), equalTo("false"));
    }

    @Test
    public void parse_trailing() throws Exception {
        ParsedArgument<Object, Boolean> parse = type.parse("false hello world", context);
        assertThat(parse.getResult(), is(false));
        assertThat(parse.getRaw(), equalTo("false"));
    }

    @Test
    public void parse_invalid() throws Exception {
        try {
            type.parse("tuesday", context);
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(ERROR_INVALID));
            assertThat(ex.getData(), equalTo(Collections.emptyMap()));
        }
    }

    @Test
    public void parse_empty() throws Exception {
        try {
            type.parse("", context);
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(ERROR_INVALID));
            assertThat(ex.getData(), equalTo(Collections.emptyMap()));
        }
    }

    @Test
    public void parse_empty_remaining() throws Exception {
        try {
            type.parse(" true", context);
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(ERROR_INVALID));
            assertThat(ex.getData(), equalTo(Collections.emptyMap()));
        }
    }

    @Test
    public void usageText() throws Exception {
        assertThat(type.getUsageText(), equalTo("bool"));
    }
}