// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class SuggestionsBuilderTest {
    private SuggestionsBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new SuggestionsBuilder("Hello w", 6);
    }

    @Test
    public void suggest_appends() {
        final Suggestions result = builder.suggest("world!").build();
        assertThat(result.getList(), equalTo(Lists.newArrayList(new Suggestion(StringRange.between(6, 7), "world!"))));
        assertThat(result.getRange(), equalTo(StringRange.between(6, 7)));
        assertThat(result.isEmpty(), is(false));
    }

    @Test
    public void suggest_replaces() {
        final Suggestions result = builder.suggest("everybody").build();
        assertThat(result.getList(), equalTo(Lists.newArrayList(new Suggestion(StringRange.between(6, 7), "everybody"))));
        assertThat(result.getRange(), equalTo(StringRange.between(6, 7)));
        assertThat(result.isEmpty(), is(false));
    }

    @Test
    public void suggest_noop() {
        final Suggestions result = builder.suggest("w").build();
        assertThat(result.getList(), equalTo(Lists.newArrayList()));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void suggest_multiple() {
        final Suggestions result = builder.suggest("world!").suggest("everybody").suggest("weekend").build();
        assertThat(result.getList(), equalTo(Lists.newArrayList(new Suggestion(StringRange.between(6, 7), "everybody"), new Suggestion(StringRange.between(6, 7), "weekend"), new Suggestion(StringRange.between(6, 7), "world!"))));
        assertThat(result.getRange(), equalTo(StringRange.between(6, 7)));
        assertThat(result.isEmpty(), is(false));
    }

    @Test
    public void restart() {
        builder.suggest("won't be included in restart");
        final SuggestionsBuilder other = builder.restart();
        assertThat(other, is(not(builder)));
        assertThat(other.getInput(), equalTo(builder.getInput()));
        assertThat(other.getStart(), is(builder.getStart()));
        assertThat(other.getRemaining(), equalTo(builder.getRemaining()));
    }

    @Test
    public void sort_alphabetical() {
        Suggestions result = builder.suggest("2").suggest("4").suggest("6").suggest("8").suggest("30").suggest("32").build();
        List<String> actual = result.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        assertThat(actual, equalTo(Lists.newArrayList( "2", "30", "32", "4", "6", "8")));
    }

    @Test
    public void sort_numerical() {
        Suggestions result = builder.suggest(2).suggest(4).suggest(6).suggest(8).suggest(30).suggest(32).build();
        List<String> actual = result.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        assertThat(actual, equalTo(Lists.newArrayList( "2", "4", "6", "8", "30", "32")));
    }

    @Test
    public void sort_mixed() {
        Suggestions result = builder.suggest("11").suggest("22").suggest("33").suggest("a").suggest("b").suggest("c").suggest(2).suggest(4).suggest(6).suggest(8).suggest(30).suggest(32).suggest("3a").suggest("a3").build();
        List<String> actual = result.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
        assertThat(actual, equalTo(Lists.newArrayList( "11", "2", "22", "33", "3a", "4", "6", "8", "30", "32", "a", "a3", "b", "c")));
    }
}
