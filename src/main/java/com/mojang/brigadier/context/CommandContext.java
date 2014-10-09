package com.mojang.brigadier.context;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Primitives;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Map;

public class CommandContext<T> {
    private final Joiner JOINER = Joiner.on(CommandDispatcher.ARGUMENT_SEPARATOR);

    private final T source;
    private final Map<String, ParsedArgument<?>> arguments;
    private final Command command;
    private final Map<CommandNode, String> nodes;

    public CommandContext(T source, Map<String, ParsedArgument<?>> arguments, Command command, Map<CommandNode, String> nodes) {
        this.source = source;
        this.arguments = arguments;
        this.command = command;
        this.nodes = nodes;
    }

    public Command getCommand() {
        return command;
    }

    public T getSource() {
        return source;
    }

    @SuppressWarnings("unchecked")
    public <V> ParsedArgument<V> getArgument(String name, Class<V> clazz) {
        ParsedArgument<?> argument = arguments.get(name);

        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        }

        if (Primitives.wrap(clazz).isAssignableFrom(argument.getResult().getClass())) {
            return (ParsedArgument<V>) argument;
        } else {
            throw new IllegalArgumentException("Argument '" + name + "' is defined as " + argument.getResult().getClass().getSimpleName() + ", not " + clazz);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandContext)) return false;

        CommandContext that = (CommandContext) o;

        if (!arguments.equals(that.arguments)) return false;
        if (!Iterables.elementsEqual(nodes.entrySet(), that.nodes.entrySet())) return false;
        if (command != null ? !command.equals(that.command) : that.command != null) return false;
        if (!source.equals(that.source)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + arguments.hashCode();
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + nodes.hashCode();
        return result;
    }

    public String getInput() {
        return JOINER.join(nodes.values());
    }

    public Map<CommandNode, String> getNodes() {
        return nodes;
    }
}
