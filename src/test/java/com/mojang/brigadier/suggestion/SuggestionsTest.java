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
        final Suggestions merged = Suggestions.merge("foo b", Collections.emptyList());
        assertThat(merged.isEmpty(), is(true));
    }

    @Test
    public void merge_single() {
        final Suggestions suggestions = new Suggestions(new StringRange(5, 5), Lists.newArrayList("ar"));
        final Suggestions merged = Suggestions.merge("foo b", Collections.singleton(suggestions));
        assertThat(merged, equalTo(suggestions));
    }

    @Test
    public void merge_multiple() {
        final Suggestions a = new Suggestions(new StringRange(5, 5), Lists.newArrayList("ar", "az"));
        final Suggestions b = new Suggestions(new StringRange(4, 5), Lists.newArrayList("foo", "qux", "apple"));
        final Suggestions merged = Suggestions.merge("foo b", Lists.newArrayList(a, b));
        assertThat(merged.getList(), equalTo(Lists.newArrayList("apple", "bar", "baz", "foo", "qux")));
    }
}