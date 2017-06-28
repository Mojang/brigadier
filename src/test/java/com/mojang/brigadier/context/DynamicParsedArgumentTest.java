package com.mojang.brigadier.context;

import com.google.common.testing.EqualsTester;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DynamicParsedArgumentTest {
    private DynamicParsedArgument<Object> subject;
    @Mock
    private Supplier<Object> supplier;

    @Before
    public void setUp() throws Exception {
        subject = new DynamicParsedArgument<>("raw", supplier);
    }

    @Test
    public void suppliedOnce() throws Exception {
        Object result = new Object();
        when(supplier.get()).thenReturn(result);

        assertThat("first evaluation", subject.getResult(), is(result));
        assertThat("already evaluated", subject.getResult(), is(result));

        verify(supplier, times(1)).get();
    }

    @Test
    public void copy() throws Exception {
        Object result = new Object();
        when(supplier.get()).thenReturn(result);
        assertThat(subject.getResult(), is(result));

        Object newResult = new Object();
        when(supplier.get()).thenReturn(newResult);
        ParsedArgument<Object> copy = subject.copy();
        assertThat(copy.getResult(), is(newResult));

        assertThat(copy, is(equalTo(subject)));

        verify(supplier, times(2)).get();
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