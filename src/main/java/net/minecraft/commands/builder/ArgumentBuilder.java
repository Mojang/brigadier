package net.minecraft.commands.builder;

import net.minecraft.commands.Command;
import net.minecraft.commands.tree.CommandNode;
import net.minecraft.commands.tree.RootCommandNode;

import java.util.List;

public abstract class ArgumentBuilder<T extends ArgumentBuilder<?>> {
    private final RootCommandNode arguments = new RootCommandNode();
    private Command command;

    protected abstract T getThis();

    public T then(ArgumentBuilder argument) {
        arguments.addChild(argument.build());
        return getThis();
    }

    public List<CommandNode> getArguments() {
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
