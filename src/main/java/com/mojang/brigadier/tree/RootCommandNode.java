package com.mojang.brigadier.tree;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class RootCommandNode<S> extends CommandNode<S> {
    public RootCommandNode() {
        super(null, c -> true, null, Collections::singleton);
    }

    @Override
    protected Object getMergeKey() {
        throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
    }

    @Override
    public String getUsageText() {
        return "";
    }

    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandException {
    }

    @Override
    public void listSuggestions(final String command, final Set<String> output, final CommandContextBuilder<S> contextBuilder) {
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCommandNode)) return false;
        return super.equals(o);
    }

    @Override
    public ArgumentBuilder<S, ?> createBuilder() {
        throw new IllegalStateException("Cannot convert root into a builder");
    }

    @Override
    protected String getSortedKey() {
        return "";
    }
}
