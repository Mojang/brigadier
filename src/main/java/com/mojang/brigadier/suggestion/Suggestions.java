// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.StringRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A collection of {@link Suggestion}s.
 */
public class Suggestions {
    private static final Suggestions EMPTY = new Suggestions(StringRange.at(0), new ArrayList<>());

    private final StringRange range;
    private final List<Suggestion> suggestions;

    public Suggestions(final StringRange range, final List<Suggestion> suggestions) {
        this.range = range;
        this.suggestions = suggestions;
    }

    /**
     * Returns the range the suggestions span.
     *
     * @return the range the suggestions span
     */
    public StringRange getRange() {
        return range;
    }

    /**
     * Returns all suggestions as a list.
     *
     * @return all suggestions as a list
     */
    public List<Suggestion> getList() {
        return suggestions;
    }

    /**
     * Checks if no suggestions are stored in this object..
     *
     * @return true if no suggestions are stored in this object
     */
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
        return Objects.equals(range, that.range) &&
                Objects.equals(suggestions, that.suggestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, suggestions);
    }

    @Override
    public String toString() {
        return "Suggestions{" +
                "range=" + range +
                ", suggestions=" + suggestions +
                '}';
    }

    /**
     * Returns a future that instantly completes with an empty Suggestions instance.
     *
     * @return a future that instantly completes with an empty Suggestions instance
     */
    public static CompletableFuture<Suggestions> empty() {
        return CompletableFuture.completedFuture(EMPTY);
    }

    /**
     * Merges multiple Suggestions instances for a single command.
     * <p>
     * Just combines the input into one collection and then calls {@link #create} with the result.
     *
     * @param command the command
     * @param input the suggestions instances
     * @return returns a merged Suggestions instance
     */
    public static Suggestions merge(final String command, final Collection<Suggestions> input) {
        if (input.isEmpty()) {
            return EMPTY;
        } else if (input.size() == 1) {
            return input.iterator().next();
        }

        final Set<Suggestion> texts = new HashSet<>();
        for (final Suggestions suggestions : input) {
            texts.addAll(suggestions.getList());
        }
        return create(command, texts);
    }

    /**
     * Creates a Suggestions instance from a command a list of possible Suggestions.
     * <p>
     * The Suggestions instance will span the entire range, from the minimal index to maximum index found
     * in the suggestions.
     * <p>
     * It will also automatically call {@link Suggestion#expand} for every possible suggestion, using the command and
     * the computed general range.
     * <p>
     * After all of that it sorts the results and returns them.
     * <p>
     * <br>Some examples:
     * <pre>
     *     Suggestion foo = new Suggestion(StringRange.between(0, 2), "foo");
     *     Suggestion bar = new Suggestion(StringRange.at(2), "bar");
     *
     *     Suggestions suggestions = Suggestions.create("1234567", List.of(foo, bar));
     *     for (Suggestion suggestion : suggestions.getList()) {
     *         System.out.println(suggestion.apply("abcdefgh"));
     *     }
     * </pre>
     * Prints:
     * <pre>
     *     (ordered alphabetically)
     *     12barcdefgh (range expanded from 0 to 2, so it replaced "ab"
     *     foocdefgh (range was already 0 to 2, so nothing really changed
     * </pre>
     *
     * @param command the command to get them for
     * @param suggestions a list with possible suggestions
     * @return a Suggestions instance for the given command with the passed suggestions
     */
    public static Suggestions create(final String command, final Collection<Suggestion> suggestions) {
        if (suggestions.isEmpty()) {
            return EMPTY;
        }
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for (final Suggestion suggestion : suggestions) {
            start = Math.min(suggestion.getRange().getStart(), start);
            end = Math.max(suggestion.getRange().getEnd(), end);
        }
        final StringRange range = new StringRange(start, end);
        final Set<Suggestion> texts = new HashSet<>();
        for (final Suggestion suggestion : suggestions) {
            texts.add(suggestion.expand(command, range));
        }
        final List<Suggestion> sorted = new ArrayList<>(texts);
        sorted.sort((a, b) -> a.compareToIgnoreCase(b));
        return new Suggestions(range, sorted);
    }
}
