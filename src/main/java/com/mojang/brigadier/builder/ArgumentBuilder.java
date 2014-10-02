package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.Collection;

public abstract class ArgumentBuilder<T extends ArgumentBuilder<?>> {
    private final RootCommandNode arguments = new RootCommandNode();
    private Command command;

    protected abstract T getThis();

    public T then(ArgumentBuilder argument) {
        arguments.addChild(argument.build());
        return getThis();
    }

    public Collection<CommandNode> getArguments() {
        return arguments.getChildren();
    }

    public T executes(Command command) {
        this.command = command;
        return getThis();
    }

    public Command getCommand() {
        return command;
    }

    public abstract CommandNode build();
}
