package com.mojang.brigadier.tree;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class CommandNode<S> {
    private final Map<Object, CommandNode<S>> children = Maps.newLinkedHashMap();
    private Command<S> command;

    protected CommandNode(Command<S> command) {
        this.command = command;
    }

    public Command<S> getCommand() {
        return command;
    }

    public Collection<CommandNode<S>> getChildren() {
        return children.values();
    }

    public void addChild(CommandNode<S> node) {
        CommandNode<S> child = children.get(node.getMergeKey());
        if (child != null) {
            // We've found something to merge onto
            if (node.getCommand() != null) {
                child.command = node.getCommand();
            }
            for (CommandNode<S> grandchild : node.getChildren()) {
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

        CommandNode<S> that = (CommandNode<S>) o;

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

    public abstract String parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException;

    public abstract void listSuggestions(String command, Set<String> output);
}
