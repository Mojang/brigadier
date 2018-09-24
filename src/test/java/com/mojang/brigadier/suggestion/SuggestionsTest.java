// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

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
        final Suggestions suggestions = new Suggestions(StringRange.at(5), Lists.newArrayList(new Suggestion(StringRange.at(5), "ar")));
        final Suggestions merged = Suggestions.merge("foo b", Collections.singleton(suggestions));
        assertThat(merged, equalTo(suggestions));
    }

    @Test
    public void merge_multiple() {
        final Suggestions a = new Suggestions(StringRange.at(5), Lists.newArrayList(new Suggestion(StringRange.at(5), "ar"), new Suggestion(StringRange.at(5), "az"), new Suggestion(StringRange.at(5), "Az")));
        final Suggestions b = new Suggestions(StringRange.between(4, 5), Lists.newArrayList(new Suggestion(StringRange.between(4, 5), "foo"), new Suggestion(StringRange.between(4, 5), "qux"), new Suggestion(StringRange.between(4, 5), "apple"), new Suggestion(StringRange.between(4, 5), "Bar")));
        final Suggestions merged = Suggestions.merge("foo b", Lists.newArrayList(a, b));
        assertThat(merged.getList(), equalTo(Lists.newArrayList(new Suggestion(StringRange.between(4, 5), "apple"), new Suggestion(StringRange.between(4, 5), "bar"), new Suggestion(StringRange.between(4, 5), "Bar"), new Suggestion(StringRange.between(4, 5), "baz"), new Suggestion(StringRange.between(4, 5), "bAz"), new Suggestion(StringRange.between(4, 5), "foo"), new Suggestion(StringRange.between(4, 5), "qux"))));
    }
}