// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

/**
 * A {@link Message} that has a literal string it returns.
 */
public class LiteralMessage implements Message {
    private final String string;

    /**
     * Creates a new {@link LiteralMessage} returning the given string.
     *
     * @param string the string to return
     */
    public LiteralMessage(final String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}
