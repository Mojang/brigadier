package net.minecraft.commands.builder;

import com.google.common.collect.Lists;
import net.minecraft.commands.Command;
import net.minecraft.commands.tree.CommandNode;

import java.util.List;

public abstract class ArgumentBuilder<T extends ArgumentBuilder<?>> {
    private final List<ArgumentBuilder> arguments = Lists.newArrayList();
    private Command command;

    protected abstract T getThis();

    public T then(ArgumentBuilder argument) {
        arguments.add(argument);
        return getThis();
    }

    public List<ArgumentBuilder> getArguments() {
        return arguments;
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
