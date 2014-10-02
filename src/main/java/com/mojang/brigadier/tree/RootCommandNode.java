package com.mojang.brigadier.tree;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;

public class RootCommandNode extends CommandNode {
    public RootCommandNode() {
        super(null);
    }

    @Override
    protected Object getMergeKey() {
        throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
    }

    @Override
    public String parse(String command, CommandContextBuilder<?> contextBuilder) throws CommandException {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCommandNode)) return false;
        return super.equals(o);
    }
}
