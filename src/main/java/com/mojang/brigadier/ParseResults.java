package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collections;
import java.util.Map;

public class ParseResults<S> {
    private final CommandContextBuilder<S> context;
    private final String remaining;
    private final Map<CommandNode<S>, CommandException> exceptions;

    public ParseResults(CommandContextBuilder<S> context, String remaining, Map<CommandNode<S>, CommandException> exceptions) {
        this.context = context;
        this.remaining = remaining;
        this.exceptions = exceptions;
    }

    public ParseResults(CommandContextBuilder<S> context) {
        this(context, "", Collections.emptyMap());
    }

    public CommandContextBuilder<S> getContext() {
        return context;
    }

    public String getRemaining() {
        return remaining;
    }

    public Map<CommandNode<S>, CommandException> getExceptions() {
        return exceptions;
    }
}
