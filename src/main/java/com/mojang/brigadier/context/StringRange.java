// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.ImmutableStringReader;

import java.util.Objects;

/**
 * A range within a string, i.e. the start and end values of a substring.
 */
public class StringRange {
    private final int start;
    private final int end;

    /**
     * Creates a new StringRange with the given start and end index.
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
     * Returns a StringRange containing <strong>no</strong> index,
     *
     * @param pos the position the range should include
     * @return a StringRange that includes no index
     */
    public static StringRange at(final int pos) {
        return new StringRange(pos, pos);
    }

    /**
     * Returns a StringRange with the given start and end indices.
     *
     * @param start the start index (inclusive)
     * @param end the end index (exclusive)
     * @return a StringRange spanning the given range
     */
    public static StringRange between(final int start, final int end) {
        return new StringRange(start, end);
    }

    /**
     * Returns a StringRange} that wraps around both passed StringRanges.
     *
     * @param a the first StringRange
     * @param b the second StringRange
     * @return a StringRange that includes everything from the lowest (inclusive) to the highest index (exclusive)
     * from both ranges
     */
    public static StringRange encompassing(final StringRange a, final StringRange b) {
        return new StringRange(Math.min(a.getStart(), b.getStart()), Math.max(a.getEnd(), b.getEnd()));
    }

    /**
     * Returns the start of this StringRange (inclusive).
     *
     * @return the start of this StringRange (inclusive)
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end of this StringRange (exclusive).
     *
     * @return the end of this StringRange (exclusive)
     */
    public int getEnd() {
        return end;
    }

    /**
     * Applies this range to the complete String of the passed {@link ImmutableStringReader}, returning all
     * characters from it that lie within this range.
     * <p>
     * Equivalent to: {@code reader.getString().substring(range.getStart(), range.getEnd()}
     *
     * @param reader the string reader to read from
     * @return the substring of the passed reader that is defined by this range
     */
    public String get(final ImmutableStringReader reader) {
        return reader.getString().substring(start, end);
    }

    /**
     * Returns the substring of the passed string, that is defined by this range.
     * <p>
     * Equivalent to: {@code string.substring(range.getStart(), range.getEnd()}
     *
     * @param string the string to get the substring from
     * @return all characters in this range in the passed string
     */
    public String get(final String string) {
        return string.substring(start, end);
    }

    /**
     * Checks if this range is empty, i.e. contains not even a single index.
     *
     * @return true if this range is empty, i.e. contains not even a single index
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
