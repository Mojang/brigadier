// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collections;
import java.util.Map;

public class ParseResults<S, R> {
    private final CommandContextBuilder<S, R> context;
    private final Map<CommandNode<S, R>, CommandSyntaxException> exceptions;
    private final ImmutableStringReader reader;

    public ParseResults(final CommandContextBuilder<S, R> context, final ImmutableStringReader reader, final Map<CommandNode<S, R>, CommandSyntaxException> exceptions) {
        this.context = context;
        this.reader = reader;
        this.exceptions = exceptions;
    }

    public ParseResults(final CommandContextBuilder<S, R> context) {
        this(context, new StringReader(""), Collections.emptyMap());
    }

    public CommandContextBuilder<S, R> getContext() {
        return context;
    }

    public ImmutableStringReader getReader() {
        return reader;
    }

    public Map<CommandNode<S, R>, CommandSyntaxException> getExceptions() {
        return exceptions;
    }
}
