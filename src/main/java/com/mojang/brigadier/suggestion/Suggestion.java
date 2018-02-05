package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.StringRange;

import java.util.Objects;

public class Suggestion implements Comparable<Suggestion> {
    private final StringRange range;
    private final String text;

    public Suggestion(final StringRange range, final String text) {
        this.range = range;
        this.text = text;
    }

    public StringRange getRange() {
        return range;
    }

    public String getText() {
        return text;
    }

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
        return Objects.equals(range, that.range) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, text);
    }

    @Override
    public String toString() {
        return "Suggestion{" +
            "range=" + range +
            ", text='" + text + '\'' +
            '}';
    }

    @Override
    public int compareTo(final Suggestion o) {
        return text.compareTo(o.text);
    }

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
        return new Suggestion(range, result.toString());
    }
}
