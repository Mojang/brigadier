// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

public class Dynamic3CommandExceptionType implements CommandExceptionType {
    private final Function function;

    public Dynamic3CommandExceptionType(final Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(final Object a, final Object b, final Object c) {
        return new CommandSyntaxException(this, function.apply(a, b, c));
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader, final Object a, final Object b, final Object c) {
        return new CommandSyntaxException(this, function.apply(a, b, c), reader.getString(), reader.getCursor());
    }

    public interface Function {
        Message apply(Object a, Object b, Object c);
    }
}
