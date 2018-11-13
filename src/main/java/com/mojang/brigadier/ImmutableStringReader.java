// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

/**
 * A string reader can be used to progressively read through a string.
 * To do this the string reader has the concept of a "cursor", which is the current position in the input string.
 * All operations not explicitly stating otherwise, like {@link #peek} or {@link #canRead}, are relative to the
 * cursor position.
 */
public interface ImmutableStringReader {
    /**
     * Returns the full input string.
     *
     * @return the full input string
     */
    String getString();

    /**
     * Returns the remaining length, so the distance from the cursor to the end of the input.
     *
     * @return the remaining length, so the distance from the cursor to the end of the input
     */
    int getRemainingLength();

    /**
     * Returns the total length of the input.
     *
     * @return the total length of the input
     */
    int getTotalLength();

    /**
     * Returns the current cursor position.
     *
     * @return the current cursor position
     */
    int getCursor();

    /**
     * Returns the part of the input that was already read.
     *
     * @return the part of the input that was already read
     */
    String getRead();

    /**
     * Returns the part of the input that was not yet read.
     *
     * @return the part of the input that was not yet read
     */
    String getRemaining();

    /**
     * Checks if the reader has enough input to read {@code length} more characters.
     *
     * @param length the amount of characters to read
     * @return true if the reader has enough input to read {@code length} more characters
     */
    boolean canRead(int length);

    /**
     * Checks if the reader can read at least one more character.
     *
     * @return true if the reader can read at least one more character
     * @see #canRead(int)
     */
    boolean canRead();

    /**
     * Returns the next character but without consuming it (so the cursor stays at its current position).
     *
     * @return the next character
     * @see #peek(int)
     */
    char peek();

    /**
     * Returns the character {@code offset} places from the cursor position without consuming it
     * (so the cursor stays at its current position).
     *
     * @param offset the offset of the character to peek at
     * @return the character {@code offset} places from the current cursor position
     */
    char peek(int offset);
}
