package com.mojang.brigadier.suggestion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.brigadier.context.StringRange;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Suggestions {
    private static final Suggestions EMPTY = new Suggestions("", Lists.newArrayList());

    private final String input;
    private final StringRange range;
    private final List<Suggestion> suggestions;

    public Suggestions(final String input, final List<Suggestion> suggestions) {
        this.input = input;
        this.suggestions = suggestions;

        if (suggestions.isEmpty()) {
            range = new StringRange(input.length(), input.length());
        } else {
            int start = Integer.MAX_VALUE;
            int end = Integer.MIN_VALUE;
            for (final Suggestion suggestion : suggestions) {
                start = Math.min(start, suggestion.getRange().getStart());
                end = Math.max(end, suggestion.getRange().getEnd());
            }
            range = new StringRange(start, end);
        }
    }

    public String getInput() {
        return input;
    }

    public StringRange getRange() {
        return range;
    }

    public List<Suggestion> getList() {
        return suggestions;
    }

    public boolean isEmpty() {
        return suggestions.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Suggestions)) {
            return false;
        }
        final Suggestions that = (Suggestions) o;
        return Objects.equals(input, that.input) &&
            Objects.equals(range, that.range) &&
            Objects.equals(suggestions, that.suggestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, range, suggestions);
    }

    public static CompletableFuture<Suggestions> empty() {
        return CompletableFuture.completedFuture(EMPTY);
    }

    public static Suggestions merge(final Collection<Suggestions> inputs) {
        if (inputs.isEmpty()) {
            return EMPTY;
        } else if (inputs.size() == 1) {
            return inputs.iterator().next();
        }

        final Set<Suggestion> suggestions = Sets.newHashSet();
        for (final Suggestions input : inputs) {
            suggestions.addAll(input.getList());
        }
        final List<Suggestion> sorted = Lists.newArrayList(suggestions);
        Collections.sort(sorted);
        return new Suggestions(inputs.iterator().next().getInput(), sorted);
    }
}
