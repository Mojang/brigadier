// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

/**
 * A {@link CommandExceptionType} taking four inputs and returning a message based on those.
 * <p>
 * The arguments can be used to e.g. format a message and display the input as well as why it did not match some
 * criteria.
 */
public class Dynamic4CommandExceptionType implements CommandExceptionType {
    private final Function function;

    public Dynamic4CommandExceptionType(final Function function) {
        this.function = function;
    }

    /**
     * Creates a {@link CommandSyntaxException} using the four passed arguments.
     *
     * @param a the first argument
     * @param b the second argument
     * @param c the third argument
     * @param d the fourth argument
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException create(final Object a, final Object b, final Object c, final Object d) {
        return new CommandSyntaxException(this, function.apply(a, b, c, d));
    }

    /**
     * Creates a {@link CommandSyntaxException} using the four passed arguments and includes information about the
     * position and input.
     *
     * @param reader the {@link ImmutableStringReader} giving information about the input and the place the error
     * occurred
     * @param a the first argument
     * @param b the second argument
     * @param c the third argument
     * @param d the fourth argument
     * @return a constructed {@link CommandSyntaxException}
     */
    public CommandSyntaxException createWithContext(final ImmutableStringReader reader, final Object a, final Object b, final Object c, final Object d) {
        return new CommandSyntaxException(this, function.apply(a, b, c, d), reader.getString(), reader.getCursor());
    }

    /**
     * A simple Function to compute a {@link Message} based on the four input arguments.
     */
    public interface Function {
        Message apply(Object a, Object b, Object c, Object d);
    }
}
