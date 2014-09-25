package net.minecraft.commands.tree;

import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public class RootCommandNode extends CommandNode {
    public RootCommandNode() {
        super(null);
    }

    @Override
    public String parse(String command, CommandContextBuilder contextBuilder) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootCommandNode)) return false;

        RootCommandNode that = (RootCommandNode) o;

        if (!getChildren().equals(that.getChildren())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getChildren().hashCode();
    }
}
