package com.mojang.brigadier.suggestion;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SuggestionsTest {
    @Test
    public void merge_empty() {
        final Suggestions merged = Suggestions.merge(Collections.emptyList());
        assertThat(merged.isEmpty(), is(true));
    }

    @Test
    public void merge_single() {
        final Suggestions suggestions = new Suggestions("", Lists.newArrayList(new Suggestion(new StringRange(0, 0), "foo")));
        final Suggestions merged = Suggestions.merge(Collections.singleton(suggestions));
        assertThat(merged, equalTo(suggestions));
    }

    @Test
    public void merge_multiple() {
        final Suggestion foo = new Suggestion(new StringRange(0, 0), "foo");
        final Suggestion bar = new Suggestion(new StringRange(0, 0), "bar");
        final Suggestion baz = new Suggestion(new StringRange(0, 0), "baz");
        final Suggestion qux = new Suggestion(new StringRange(0, 0), "qux");
        final Suggestions a = new Suggestions("", Lists.newArrayList(foo, bar));
        final Suggestions b = new Suggestions("", Lists.newArrayList(baz, qux));
        final Suggestions merged = Suggestions.merge(Lists.newArrayList(a, b));
        assertThat(merged.getList(), equalTo(Lists.newArrayList(bar, baz, foo, qux)));
    }
}