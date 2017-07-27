package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collections;
import java.util.Map;

public class ParseResults<S> {
    private final CommandContextBuilder<S> context;
    private final Map<CommandNode<S>, CommandException> exceptions;
    private final ImmutableStringReader reader;

    public ParseResults(final CommandContextBuilder<S> context, final ImmutableStringReader reader, final Map<CommandNode<S>, CommandException> exceptions) {
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

    public Map<CommandNode<S>, CommandException> getExceptions() {
        return exceptions;
    }
}
