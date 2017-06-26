package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.Collection;

public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S, ?>> {
    private final RootCommandNode<S> arguments = new RootCommandNode<>();
    private Command<S> command;

    protected abstract T getThis();

    public T then(ArgumentBuilder<S, ?> argument) {
        arguments.addChild(argument.build());
        return getThis();
    }

    public Collection<CommandNode<S>> getArguments() {
        return arguments.getChildren();
    }

    public T executes(Command<S> command) {
        this.command = command;
        return getThis();
    }

    protected Command<S> getCommand() {
        return command;
    }

    public abstract CommandNode<S> build();
}
