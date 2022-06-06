// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class SuggestionsBuilder {
    private final String input;
    private final String inputLowerCase;
    private final int start;
    private final String remaining;
    private final String remainingLowerCase;
    private final List<Suggestion> result = new ArrayList<>();

    public SuggestionsBuilder(final String input, final String inputLowerCase, final int start) {
        this.input = input;
        this.inputLowerCase = inputLowerCase;
        this.start = start;
        this.remaining = input.substring(start);
        this.remainingLowerCase = inputLowerCase.substring(start);
    }

    public SuggestionsBuilder(final String input, final int start) {
        this(input, input.toLowerCase(Locale.ROOT), start);
    }

    public String getInput() {
        return input;
    }

    public int getStart() {
        return start;
    }

    public String getRemaining() {
        return remaining;
    }

    public String getRemainingLowerCase() {
        return remainingLowerCase;
    }

    public Suggestions build() {
        return Suggestions.create(input, result);
    }

    public CompletableFuture<Suggestions> buildFuture() {
        return CompletableFuture.completedFuture(build());
    }

    public SuggestionsBuilder suggest(final String text) {
        if (text.equals(remaining)) {
            return this;
        }
        result.add(new Suggestion(StringRange.between(start, input.length()), text));
        return this;
    }

    public SuggestionsBuilder suggest(final String text, final Message tooltip) {
        if (text.equals(remaining)) {
            return this;
        }
        result.add(new Suggestion(StringRange.between(start, input.length()), text, tooltip));
        return this;
    }

    public SuggestionsBuilder suggest(final int value) {
        result.add(new IntegerSuggestion(StringRange.between(start, input.length()), value));
        return this;
    }

    public SuggestionsBuilder suggest(final int value, final Message tooltip) {
        result.add(new IntegerSuggestion(StringRange.between(start, input.length()), value, tooltip));
        return this;
    }

    public SuggestionsBuilder add(final SuggestionsBuilder other) {
        result.addAll(other.result);
        return this;
    }

    public SuggestionsBuilder createOffset(final int start) {
        return new SuggestionsBuilder(input, inputLowerCase, start);
    }

    public SuggestionsBuilder restart() {
        return createOffset(start);
    }
}
