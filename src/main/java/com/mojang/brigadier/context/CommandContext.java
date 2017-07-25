package com.mojang.brigadier.context;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Map;

public class CommandContext<S> {
    private final S source;
    private final Command<S> command;
    private final Map<String, ParsedArgument<S, ?>> arguments;
    private final Map<CommandNode<S>, String> nodes;
    private final String input;

    public CommandContext(final S source, final Map<String, ParsedArgument<S, ?>> arguments, final Command<S> command, final Map<CommandNode<S>, String> nodes, final String input) {
        this.source = source;
        this.arguments = arguments;
        this.command = command;
        this.nodes = nodes;
        this.input = input;
    }

    public Command<S> getCommand() {
        return command;
    }

    public S getSource() {
        return source;
    }

    @SuppressWarnings("unchecked")
    public <V> V getArgument(final String name, final Class<V> clazz) {
        final ParsedArgument<S, ?> argument = arguments.get(name);

        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        }

        final Object result = argument.getResult();
        if (Primitives.wrap(clazz).isAssignableFrom(result.getClass())) {
            return (V) result;
        } else {
            throw new IllegalArgumentException("Argument '" + name + "' is defined as " + result.getClass().getSimpleName() + ", not " + clazz);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandContext)) return false;

        final CommandContext that = (CommandContext) o;

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
        return input;
    }

    public Map<CommandNode<S>, String> getNodes() {
        return nodes;
    }

    public CommandContext<S> copy() {
        final Map<String, ParsedArgument<S, ?>> arguments = Maps.newLinkedHashMap();
        this.arguments.forEach((k, v) -> arguments.put(k, v.copy()));
        return new CommandContext<>(source, arguments, command, nodes, input);
    }
}
