package net.minecraft.commands.tree;

import com.google.common.collect.Lists;
import net.minecraft.commands.Command;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

import java.util.List;

public abstract class CommandNode {
    private final Command command;
    private final List<CommandNode> children = Lists.newArrayList();

    protected CommandNode(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public List<CommandNode> getChildren() {
        return children;
    }

    public void addChild(CommandNode node) {
        children.add(node);
    }

    public abstract String parse(String command, CommandContextBuilder contextBuilder) throws IllegalArgumentSyntaxException, ArgumentValidationException;
}
