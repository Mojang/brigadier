// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.StringRange;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SuggestionTest {
    @Test
    public void apply_insertation_start() {
        final Suggestion suggestion = new Suggestion(StringRange.at(0), "And so I said: ");
        assertThat(suggestion.apply("Hello world!"), equalTo("And so I said: Hello world!"));
    }

    @Test
    public void apply_insertation_middle() {
        final Suggestion suggestion = new Suggestion(StringRange.at(6), "small ");
        assertThat(suggestion.apply("Hello world!"), equalTo("Hello small world!"));
    }

    @Test
    public void apply_insertation_end() {
        final Suggestion suggestion = new Suggestion(StringRange.at(5), " world!");
        assertThat(suggestion.apply("Hello"), equalTo("Hello world!"));
    }

    @Test
    public void apply_replacement_start() {
        final Suggestion suggestion = new Suggestion(StringRange.between(0, 5), "Goodbye");
        assertThat(suggestion.apply("Hello world!"), equalTo("Goodbye world!"));
    }

    @Test
    public void apply_replacement_middle() {
        final Suggestion suggestion = new Suggestion(StringRange.between(6, 11), "Alex");
        assertThat(suggestion.apply("Hello world!"), equalTo("Hello Alex!"));
    }

    @Test
    public void apply_replacement_end() {
        final Suggestion suggestion = new Suggestion(StringRange.between(6, 12), "Creeper!");
        assertThat(suggestion.apply("Hello world!"), equalTo("Hello Creeper!"));
    }

    @Test
    public void apply_replacement_everything() {
        final Suggestion suggestion = new Suggestion(StringRange.between(0, 12), "Oh dear.");
        assertThat(suggestion.apply("Hello world!"), equalTo("Oh dear."));
    }

    @Test
    public void expand_unchanged() {
        final Suggestion suggestion = new Suggestion(StringRange.at(1), "oo");
        assertThat(suggestion.expand("f", StringRange.at(1)), equalTo(suggestion));
    }

    @Test
    public void expand_left() {
        final Suggestion suggestion = new Suggestion(StringRange.at(1), "oo");
        assertThat(suggestion.expand("f", StringRange.between(0, 1)), equalTo(new Suggestion(StringRange.between(0, 1), "foo")));
    }

    @Test
    public void expand_right() {
        final Suggestion suggestion = new Suggestion(StringRange.at(0), "minecraft:");
        assertThat(suggestion.expand("fish", StringRange.between(0, 4)), equalTo(new Suggestion(StringRange.between(0, 4), "minecraft:fish")));
    }

    @Test
    public void expand_both() {
        final Suggestion suggestion = new Suggestion(StringRange.at(11), "minecraft:");
        assertThat(suggestion.expand("give Steve fish_block", StringRange.between(5, 21)), equalTo(new Suggestion(StringRange.between(5, 21), "Steve minecraft:fish_block")));
    }

    @Test
    public void expand_replacement() {
        final Suggestion suggestion = new Suggestion(StringRange.between(6, 11), "strangers");
        assertThat(suggestion.expand("Hello world!", StringRange.between(0, 12)), equalTo(new Suggestion(StringRange.between(0, 12), "Hello strangers!")));
    }
}