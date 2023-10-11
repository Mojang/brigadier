package com.mojang.brigadier.arguments;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.mojang.brigadier.arguments.ListElementArgumentType.list;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ListElementArgumentTypeTest {
    private List<String> list;
    private ListElementArgumentType type;

    @Mock
    private CommandContextBuilder<Object> context;

    @Before
    public void setUp() {
        list = ImmutableList.of("ab", "ds", "qc", "ba", "aa", "kk", "awesome", "balls");
        type = list(list);
    }

    @Test
    public void parse() throws Exception {
        final StringReader reader = mock(StringReader.class);
        when(reader.readString()).thenReturn("kk");
        assertThat(type.parse(reader), is("kk"));
        verify(reader).readString();
    }

    @Test
    public void suggest() {
        assertArrayEquals(
            new String[]{"aa", "ab", "awesome"},
            list.stream().filter(it -> it.startsWith("a")).distinct().sorted().toArray()
        );
    }
}
