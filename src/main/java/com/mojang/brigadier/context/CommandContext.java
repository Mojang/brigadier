package com.mojang.brigadier.context;

import com.google.common.primitives.Primitives;
import com.mojang.brigadier.Command;

import java.util.Map;

public class CommandContext<T> {
    private final T source;
    private final Map<String, ParsedArgument<?>> arguments;
    private final Command command;

    public CommandContext(T source, Map<String, ParsedArgument<?>> arguments, Command command) {
        this.source = source;
        this.arguments = arguments;
        this.command = command;
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
        if (command != null ? !command.equals(that.command) : that.command != null) return false;
        if (!source.equals(that.source)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + arguments.hashCode();
        result = 31 * result + (command != null ? command.hashCode() : 0);
        return result;
    }
}
