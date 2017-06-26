package com.mojang.brigadier.tree;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Set;

public class RootCommandNode<S> extends CommandNode<S> {
    public RootCommandNode() {
        super(null, c -> true);
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
    public String parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        return command;
    }

    @Override
    public void listSuggestions(String command, Set<String> output) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCommandNode)) return false;
        return super.equals(o);
    }
}
