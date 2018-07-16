package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collections;
import java.util.Map;

public class ParseResults<S> {
    private final CommandContextBuilder<S> context;
    private final Map<CommandNode<S>, CommandSyntaxException> exceptions;
    private final int startIndex;
    private final ImmutableStringReader reader;

    public ParseResults(final CommandContextBuilder<S> context, final int startIndex, final ImmutableStringReader reader, final Map<CommandNode<S>, CommandSyntaxException> exceptions) {
        this.context = context;
        this.startIndex = startIndex;
        this.reader = reader;
        this.exceptions = exceptions;
    }

    public ParseResults(final CommandContextBuilder<S> context) {
        this(context, 0, new StringReader(""), Collections.emptyMap());
    }

    public int getStartIndex() {
        return startIndex;
    }

    public CommandContextBuilder<S> getContext() {
        return context;
    }

    public ImmutableStringReader getReader() {
        return reader;
    }

    public Map<CommandNode<S>, CommandSyntaxException> getExceptions() {
        return exceptions;
    }
}
