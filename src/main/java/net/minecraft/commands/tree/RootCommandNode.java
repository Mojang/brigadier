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
}
