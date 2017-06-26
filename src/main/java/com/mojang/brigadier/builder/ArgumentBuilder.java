package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S, ?>> {
    private final RootCommandNode<S> arguments = new RootCommandNode<>();
    private Command<S> command;
    private Predicate<S> requirement = s -> true;

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

    public T requires(Predicate<S> requirement) {
        this.requirement = requirement;
        return getThis();
    }

    public Predicate<S> getRequirement() {
        return requirement;
    }

    public abstract CommandNode<S> build();
}
