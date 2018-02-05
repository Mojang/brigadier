package com.mojang.brigadier.suggestion;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import org.junit.Before;
import org.junit.Test;

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
        assertThat(result.getList(), equalTo(Lists.newArrayList("world!")));
        assertThat(result.getRange(), equalTo(StringRange.between(6, 7)));
        assertThat(result.isEmpty(), is(false));
    }

    @Test
    public void suggest_replaces() {
        final Suggestions result = builder.suggest("everybody").build();
        assertThat(result.getList(), equalTo(Lists.newArrayList("everybody")));
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
        assertThat(result.getList(), equalTo(Lists.newArrayList("everybody", "weekend", "world!")));
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
}