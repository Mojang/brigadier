package com.mojang.brigadier.tree;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class CommandNode {
    private final Map<Object, CommandNode> children = Maps.newLinkedHashMap();
    private Command command;

    protected CommandNode(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public Collection<CommandNode> getChildren() {
        return children.values();
    }

    public void addChild(CommandNode node) {
        CommandNode child = children.get(node.getMergeKey());
        if (child != null) {
            // We've found something to merge onto
            if (node.getCommand() != null) {
                child.command = node.getCommand();
            }
            for (CommandNode grandchild : node.getChildren()) {
                child.addChild(grandchild);
            }
        } else {
            children.put(node.getMergeKey(), node);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandNode)) return false;

        CommandNode that = (CommandNode) o;

        if (!children.equals(that.children)) return false;
        if (command != null ? !command.equals(that.command) : that.command != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * children.hashCode() + (command != null ? command.hashCode() : 0);
    }

    protected abstract Object getMergeKey();

    public abstract String getUsageText();

    public abstract String parse(String command, CommandContextBuilder<?> contextBuilder) throws CommandException;

    public abstract void listSuggestions(String command, Set<String> output);
}
