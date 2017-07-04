package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DynamicParsedArgumentTest {
    private DynamicParsedArgument<Object, Object> subject;
    @Mock
    private Function<Object, Object> supplier;
    @Mock
    private Object source;

    @Before
    public void setUp() throws Exception {
        subject = new DynamicParsedArgument<>("raw", supplier);
    }

    @Test
    public void suppliedOnce() throws Exception {
        Object result = new Object();
        when(supplier.apply(source)).thenReturn(result);

        assertThat("first evaluation", subject.getResult(source), is(result));
        assertThat("already evaluated", subject.getResult(source), is(result));

        verify(supplier, times(1)).apply(source);
    }

    @Test
    public void copy() throws Exception {
        Object result = new Object();
        when(supplier.apply(source)).thenReturn(result);
        assertThat(subject.getResult(source), is(result));

        Object newResult = new Object();
        when(supplier.apply(source)).thenReturn(newResult);
        ParsedArgument<Object, Object> copy = subject.copy();
        assertThat(copy.getResult(source), is(newResult));

        assertThat(copy, is(equalTo(subject)));

        verify(supplier, times(2)).apply(source);
    }

    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new FixedParsedArgument<>("foo", "bar"), new FixedParsedArgument<>("foo", "bar"))
            .addEqualityGroup(new FixedParsedArgument<>("bar", "baz"), new FixedParsedArgument<>("bar", "baz"))
            .addEqualityGroup(new FixedParsedArgument<>("foo", "baz"), new FixedParsedArgument<>("foo", "baz"))
            .testEquals();
    }
}