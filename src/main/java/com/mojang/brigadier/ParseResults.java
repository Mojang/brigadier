// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collections;
import java.util.Map;

/**
 * Consolidates the results of parsing a command in an object.
 *
 * @param <S> the type of the command source
 */
public class ParseResults<S> {
    private final CommandContextBuilder<S> context;
    private final Map<CommandNode<S>, CommandSyntaxException> exceptions;
    private final ImmutableStringReader reader;

    public ParseResults(final CommandContextBuilder<S> context, final ImmutableStringReader reader, final Map<CommandNode<S>, CommandSyntaxException> exceptions) {
        this.context = context;
        this.reader = reader;
        this.exceptions = exceptions;
    }

    public ParseResults(final CommandContextBuilder<S> context) {
        this(context, new StringReader(""), Collections.emptyMap());
    }

    /**
     * The {@link CommandContext} that was created.
     *
     * @return the created command context
     */
    public CommandContextBuilder<S> getContext() {
        return context;
    }

    /**
     * An immutable version of the string reader that was used to read the input
     *
     * @return the string reader that was used to read the input
     */
    public ImmutableStringReader getReader() {
        return reader;
    }

    /**
     * Returns all exceptions that occurred while parsing together with the node that caused them.
     *
     * @return all exceptions that occurred while parsing together with the node that caused them
     */
    public Map<CommandNode<S>, CommandSyntaxException> getExceptions() {
        return exceptions;
    }
}
