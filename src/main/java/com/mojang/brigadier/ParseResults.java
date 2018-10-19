// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNodeInterface;

import java.util.Collections;
import java.util.Map;

public class ParseResults<S> {
    private final CommandContextBuilder<S> context;
    private final Map<CommandNodeInterface<S>, CommandSyntaxException> exceptions;
    private final ImmutableStringReader reader;

    public ParseResults(final CommandContextBuilder<S> context, final ImmutableStringReader reader, final Map<CommandNodeInterface<S>, CommandSyntaxException> exceptions) {
        this.context = context;
        this.reader = reader;
        this.exceptions = exceptions;
    }

    public ParseResults(final CommandContextBuilder<S> context) {
        this(context, new StringReader(""), Collections.emptyMap());
    }

    public CommandContextBuilder<S> getContext() {
        return context;
    }

    public ImmutableStringReader getReader() {
        return reader;
    }

    public Map<CommandNodeInterface<S>, CommandSyntaxException> getExceptions() {
        return exceptions;
    }
}
