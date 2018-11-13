// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A builder to simplify creating {@link Suggestions} instances.
 */
public class SuggestionsBuilder {
    private final String input;
    private final int start;
    private final String remaining;
    private final List<Suggestion> result = new ArrayList<>();

    public SuggestionsBuilder(final String input, final int start) {
        this.input = input;
        this.start = start;
        this.remaining = input.substring(start);
    }

    /**
     * Returns the full input this builder was created with.
     *
     * @return the input
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns the start index of the suggestions in the input.
     *
     * @return the start index of the suggestions in the input
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the text that remains untouched by the suggestions
     *
     * @return the text that remains untouched by the suggestions
     */
    public String getRemaining() {
        return remaining;
    }

    /**
     * Builds a {@link Suggestions} instance based on this builder.
     *
     * @return a {@link Suggestions} instance based on this builder
     */
    public Suggestions build() {
        return Suggestions.create(input, result);
    }

    /**
     * Builds the suggestions and returns the result in a future that instantly completes.
     *
     * @return the result as a future
     */
    public CompletableFuture<Suggestions> buildFuture() {
        return CompletableFuture.completedFuture(build());
    }

    /**
     * Suggests some replacement text for the entire range from {@link #getStart()} to the end.
     *
     * @param text the text to suggest
     * @return this builder
     */
    public SuggestionsBuilder suggest(final String text) {
        if (text.equals(remaining)) {
            return this;
        }
        result.add(new Suggestion(StringRange.between(start, input.length()), text));
        return this;
    }

    /**
     * Suggests some replacement text for the entire range from {@link #getStart()} to the end and provides a user
     * readable tooltip.
     *
     * @param text the text to suggest
     * @param tooltip the tooltip to add to the suggestion
     * @return this builder
     */
    public SuggestionsBuilder suggest(final String text, final Message tooltip) {
        if (text.equals(remaining)) {
            return this;
        }
        result.add(new Suggestion(StringRange.between(start, input.length()), text, tooltip));
        return this;
    }

    /**
     * Suggests some replacement integer for the entire range from {@link #getStart()} to the end.
     *
     * @param value the value to suggest
     * @return this builder
     */
    public SuggestionsBuilder suggest(final int value) {
        result.add(new IntegerSuggestion(StringRange.between(start, input.length()), value));
        return this;
    }

    /**
     * Suggests some replacement integer for the entire range from {@link #getStart()} to the end and provides a user
     * readable tooltip.
     *
     * @param value the integer to suggest
     * @param tooltip the tooltip to add to the suggestion
     * @return this builder
     */
    public SuggestionsBuilder suggest(final int value, final Message tooltip) {
        result.add(new IntegerSuggestion(StringRange.between(start, input.length()), value, tooltip));
        return this;
    }

    /**
     * Adds all suggestions from another SuggestionsBuilder.
     *
     * @param other the other builder
     * @return this builder
     */
    public SuggestionsBuilder add(final SuggestionsBuilder other) {
        result.addAll(other.result);
        return this;
    }

    /**
     * Create a new SuggestionsBuilder for the same input but a new start index.
     *
     * @param start the new start index
     * @return the new builder with the same input, no suggestions and starting at {@code start}
     */
    public SuggestionsBuilder createOffset(final int start) {
        return new SuggestionsBuilder(input, start);
    }

    /**
     * Creates a builder with the same input and start index but no suggestions.
     * <p>
     * It effectively creates a copy with nothing set, hence the term "restart".
     *
     * @return the new builder with the same input and start index, but no completions
     * @see #createOffset
     */
    public SuggestionsBuilder restart() {
        return new SuggestionsBuilder(input, start);
    }
}
