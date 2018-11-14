// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

/**
 * A simple {@link CommandExceptionType} that gives a static message, that does not change based on the type of error.
 * <p>
 * An example could be a message displaying a command was not found, but not including the full input again.
 */
public class SimpleCommandExceptionType implements CommandExceptionType {
    private final Message message;

    public SimpleCommandExceptionType(final Message message) {
        this.message = message;
    }

    /**
     * Creates a {@link CommandSyntaxException}.
     *
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException create() {
        return new CommandSyntaxException(this, message);
    }

    /**
     * Creates a {@link CommandSyntaxException} including information about the position and input.
     *
     * @param reader the {@link ImmutableStringReader} giving information about the input and the place the error
     * occurred
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException createWithContext(final ImmutableStringReader reader) {
        return new CommandSyntaxException(this, message, reader.getString(), reader.getCursor());
    }

    @Override
    public String toString() {
        return message.getString();
    }
}
