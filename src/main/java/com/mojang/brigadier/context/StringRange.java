// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;

import java.util.Objects;

/**
 * A range within a string, i.e. the start and end values of a substring.
 */
public class StringRange {
    private final int start;
    private final int end;

    /**
     * Creates a new {@link StringRange} with the given start and end index.
     *
     * @param start the start index (inclusive)
     * @param end the end index (exclusive)
     */
    public StringRange(final int start, final int end) {
        // TODO: String ranges with start > end?
        this.start = start;
        this.end = end;
    }

    /**
     * Returns a {@link StringRange} spanning only a single index, the one given as an argument.
     *
     * @param pos the position the range should include
     * @return a {@link StringRange} that only includes the given index
     */
    public static StringRange at(final int pos) {
        return new StringRange(pos, pos);
    }

    /**
     * Returns a {@link StringRange} with the given start and end indices.
     *
     * @param start the start index (inclusive)
     * @param end the end index (exclusive)
     * @return s {@link StringRange} spanning the given range
     */
    public static StringRange between(final int start, final int end) {
        return new StringRange(start, end);
    }

    /**
     * Returns a {@link StringRange} that wraps around both passed {@link StringRange}s.
     *
     * @param a the first {@link StringRange}
     * @param b the second {@link StringRange}
     * @return a {@link StringRange} that includes everything from the lowest to highest index in both ranges
     */
    public static StringRange encompassing(final StringRange a, final StringRange b) {
        return new StringRange(Math.min(a.getStart(), b.getStart()), Math.max(a.getEnd(), b.getEnd()));
    }

    /**
     * Returns the start of this {@link StringRange} (inclusive).
     *
     * @return the start of this {@link StringRange} (inclusive).
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end of this {@link StringRange} (exclusive).
     *
     * @return the end of this {@link StringRange} (exclusive)
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the substring between the given indices from the passed {@link StringReader}.
     *
     * @param reader the string reader to read from
     * @return all characters in this range in the passed reader
     */
    public String get(final ImmutableStringReader reader) {
        return reader.getString().substring(start, end);
    }

    /**
     * Returns the substring between the given indices from the passed String.
     * <p>
     * Equivalent to: {@code string.substring(range.getStart(), range.getEnd()}
     *
     * @param string the string to read from
     * @return all characters in this range in the passed string
     */
    public String get(final String string) {
        return string.substring(start, end);
    }

    /**
     * Checks if this range is emtpy, i.e. contains not even a single index.
     *
     * @return true if this range is emtpy, i.e. contains not even a single index
     */
    public boolean isEmpty() {
        return start == end;
    }

    /**
     * Returns the length of this string range.
     *
     * @return the length of this string range
     */
    public int getLength() {
        return end - start;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringRange)) {
            return false;
        }
        final StringRange that = (StringRange) o;
        return start == that.start && end == that.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "StringRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
