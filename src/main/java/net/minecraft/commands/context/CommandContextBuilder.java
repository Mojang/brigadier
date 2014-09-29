package net.minecraft.commands.context;

import com.google.common.collect.Maps;
import net.minecraft.commands.Command;

import java.util.Map;

public class CommandContextBuilder<T> {
    private final Map<String, ParsedArgument<?>> arguments = Maps.newHashMap();
    private final T source;
    private Command command;

    public CommandContextBuilder(T source) {
        this.source = source;
    }

    public CommandContextBuilder<T> withArgument(String name, ParsedArgument<?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public Map<String, ParsedArgument<?>> getArguments() {
        return arguments;
    }

    public CommandContextBuilder<T> withCommand(Command command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder<T> copy() {
        CommandContextBuilder<T> copy = new CommandContextBuilder<T>(source);
        copy.command = this.command;
        copy.arguments.putAll(this.arguments);
        return copy;
    }

    public CommandContext<T> build() {
        return new CommandContext<T>(source, arguments, command);
    }
}
