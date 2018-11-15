// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

/**
 * A {@link CommandExceptionType} taking an arbitrary number of inputs and returning a message based on those.
 * <p>
 * The arguments can be used to e.g. format a message and display the input as well as why it did not match some
 * criteria.
 */
public class DynamicNCommandExceptionType implements CommandExceptionType {
    private final Function function;

    public DynamicNCommandExceptionType(final Function function) {
        this.function = function;
    }

    /**
     * Creates a {@link CommandSyntaxException} using the passed arguments.
     *
     * @param a the first argument
     * @param args the other arguments
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException create(final Object a, final Object... args) {
        return new CommandSyntaxException(this, function.apply(args));
    }

    /**
     * Creates a {@link CommandSyntaxException} using the passed arguments and includes information about the
     * position and input.
     *
     * @param reader the {@link ImmutableStringReader} giving information about the input and the place the error
     * occurred
     * @param args the arguments
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException createWithContext(final ImmutableStringReader reader, final Object... args) {
        return new CommandSyntaxException(this, function.apply(args), reader.getString(), reader.getCursor());
    }

    /**
     * A simple Function to compute a {@link Message} based on the input arguments.
     */
    public interface Function {
        Message apply(Object[] args);
    }
}
