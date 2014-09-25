package net.minecraft.commands.tree;

import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public class RootCommandNode extends CommandNode {
    public RootCommandNode() {
        super(null);
    }

    @Override
    protected Object getMergeKey() {
        throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
    }

    @Override
    public String parse(String command, CommandContextBuilder contextBuilder) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCommandNode)) return false;
        return super.equals(o);
    }
}
