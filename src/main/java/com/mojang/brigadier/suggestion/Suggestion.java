// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;

import java.util.Objects;

/**
 * Represents a suggestion for a possible next value as well as an optional tooltip.
 * <p>
 * An example would be tab completion showing you the suggestions on the fly.
 * <p>
 * <br>Instances of this class are compared based on the natural order of {@link #getText()}.
 * A case insensitive comparison method is provided as {@link #compareToIgnoreCase(Suggestion)}.
 */
public class Suggestion implements Comparable<Suggestion> {
    private final StringRange range;
    private final String text;
    private final Message tooltip;

    /**
     * Creates a new Suggestion spanning the given string range in the input and that has a given text it
     * suggests.
     *
     * @param range the range in the input it is applicable in
     * @param text the replacement it suggests
     */
    public Suggestion(final StringRange range, final String text) {
        this(range, text, null);
    }

    /**
     * Creates a new Suggestion spanning the given string range in the input, with a given text it suggests and
     * a tooltip it provides.
     *
     * @param range the range in the input it is applicable in
     * @param text the replacement it suggests
     * @param tooltip some explanatory tooltip to show to the user, before they apply the suggestion
     */
    public Suggestion(final StringRange range, final String text, final Message tooltip) {
        this.range = range;
        this.text = text;
        this.tooltip = tooltip;
    }

    /**
     * Returns the range in the input string this suggestion is applicable in.
     *
     * @return the range in the input it is applicable in
     */
    public StringRange getRange() {
        return range;
    }

    /**
     * Returns the text this suggestion suggests.
     *
     * @return the text this suggestion suggests
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the tooltip with some explanatory message for the user.
     *
     * @return the tooltip with some explanatory message for the user or null if not set
     */
    public Message getTooltip() {
        return tooltip;
    }

    // @formatter:off
    /**
     * Applies the suggestion to the input.
     *
     * This method will replace or insert the {@link #getText()} into the passed input string.
     *
     * <br>An Example with the text {@code fizz}:
     * <ul>
     *     <li>
     *         Input: {@code buzz}, range 0-4
     *         <br>Output: {@code fizz}
     *     </li>
     *     <li>
     *         Input: {@code buzz}, range 0-0
     *         <br>Output: {@code fizzbuzz}
     *     </li>
     *     <li>
     *         Input: {@code buzz}, range 4-4
     *         <br>Output: {@code buzzfizz}
     *     </li>
     * </ul>
     * @param input the input string to apply it to
     * @return the result of applying the suggestion to the input
     * @throws StringIndexOutOfBoundsException if the range is not contained in the input string
     */
    // @formatter:on
    public String apply(final String input) {
        if (range.getStart() == 0 && range.getEnd() == input.length()) {
            return text;
        }
        final StringBuilder result = new StringBuilder();
        if (range.getStart() > 0) {
            result.append(input.substring(0, range.getStart()));
        }
        result.append(text);
        if (range.getEnd() < input.length()) {
            result.append(input.substring(range.getEnd()));
        }
        return result.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Suggestion)) {
            return false;
        }
        final Suggestion that = (Suggestion) o;
        return Objects.equals(range, that.range) && Objects.equals(text, that.text) && Objects.equals(tooltip, that.tooltip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, text, tooltip);
    }

    @Override
    public String toString() {
        return "Suggestion{" +
            "range=" + range +
            ", text='" + text + '\'' +
            ", tooltip='" + tooltip + '\'' +
            '}';
    }

    /**
     * Compares the {@link #getText()} of this suggestion to the {@link #getText()} of the passed suggestion.
     * <p>
     * This method is case sensitive, see {@link #compareToIgnoreCase(Suggestion)} if you need it to the ignore the
     * case.
     *
     * @param o the suggestion to compare it to
     * @return -1 if this suggestion is less than the other, 0 if they are equal or 1 if this suggestion should appear
     * behind the passed one
     * @see #compareToIgnoreCase
     * @see String#compareTo
     */
    @Override
    public int compareTo(final Suggestion o) {
        return text.compareTo(o.text);
    }

    /**
     * Compares the {@link #getText()} of this suggestion to the {@link #getText()} of the passed suggestion, ignoring
     * case.
     *
     * @param b the suggestion to compare it to
     * @return -1 if this suggestion is less than the other, 0 if they are equal or 1 if this suggestion should appear
     * behind the passed one
     * @see String#compareToIgnoreCase
     * @see #compareTo
     */
    public int compareToIgnoreCase(final Suggestion b) {
        return text.compareToIgnoreCase(b.text);
    }

    // @formatter:off
    /**
     * Expands this suggestion by changing the range to the passed one and filling everything that lies outside
     * {@link #getText()} with characters from the passed command.
     *
     * <br>Examples with text = {@code ----} and range = {@code 0} and the text to apply it to as {@code abcdefghi}:
     * <ul>
     *     <li>
     *         {@code expand("123", StringRange.at(0)}:
     *         <br>{@code ----abcdefghi}, as the ranges matched and so nothing was changed
     *     </li>
     *     <li>
     *         {@code expand("123", StringRange.at(1)}:
     *         <br>{@code a----1bcdefghi}, as the passed range (1) was taken and one character from the command was
     *         used to fill up the index 1, as the range of the original suggestion was 0
     *     </li>
     *     <li>
     *         {@code expand("123", StringRange.between(1, 3)}:
     *         <br>{@code a----123defghi}, as the passed range (1,3) was taken and 3 characters from the command were
     *         used to fill up the indices 1, 2 and 3, as the range of the original suggestion was 0. It is then inserted
     *         after the first character in the text it is applied to (after the a) and replaces everything that lies in
     *         its interval (bc).
     *     </li>
     * </ul>
     * @param command the command to fill the range up with
     * @param range the range to widen it to
     * @return the expanded suggestion
     */
    // @formatter:on
    public Suggestion expand(final String command, final StringRange range) {
        if (range.equals(this.range)) {
            return this;
        }
        final StringBuilder result = new StringBuilder();
        if (range.getStart() < this.range.getStart()) {
            result.append(command.substring(range.getStart(), this.range.getStart()));
        }
        result.append(text);
        if (range.getEnd() > this.range.getEnd()) {
            result.append(command.substring(this.range.getEnd(), range.getEnd()));
        }
        return new Suggestion(range, result.toString(), tooltip);
    }
}
