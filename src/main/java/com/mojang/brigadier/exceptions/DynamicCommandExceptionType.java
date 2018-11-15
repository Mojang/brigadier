// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

import java.util.function.Function;

/**
 * A {@link CommandExceptionType} taking one input and returning a message based on it.
 * <p>
 * The argument can be used to e.g. format a message and display the input as well as why it did not match some
 * criteria.
 */
public class DynamicCommandExceptionType implements CommandExceptionType {
    private final Function<Object, Message> function;

    public DynamicCommandExceptionType(final Function<Object, Message> function) {
        this.function = function;
    }

    /**
     * Creates a {@link CommandSyntaxException} using the passed argument.
     *
     * @param arg the argument
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException create(final Object arg) {
        return new CommandSyntaxException(this, function.apply(arg));
    }

    /**
     * Creates a {@link CommandSyntaxException} using the passed argument and includes information about the
     * position and input.
     *
     * @param reader the {@link ImmutableStringReader} giving information about the input and the place the error
     * occurred
     * @param arg the argument
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException createWithContext(final ImmutableStringReader reader, final Object arg) {
        return new CommandSyntaxException(this, function.apply(arg), reader.getString(), reader.getCursor());
    }
}
