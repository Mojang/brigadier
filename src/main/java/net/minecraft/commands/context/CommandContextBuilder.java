package net.minecraft.commands.context;

import com.google.common.collect.Maps;
import net.minecraft.commands.Command;

import java.util.Map;

public class CommandContextBuilder {
    private final Map<String, ParsedArgument<?>> arguments = Maps.newHashMap();
    private Command command;

    public CommandContextBuilder() {
    }

    public CommandContextBuilder withArgument(String name, ParsedArgument<?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public Map<String, ParsedArgument<?>> getArguments() {
        return arguments;
    }

    public CommandContextBuilder withCommand(Command command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder copy() {
        CommandContextBuilder copy = new CommandContextBuilder();
        copy.command = this.command;
        copy.arguments.putAll(this.arguments);
        return copy;
    }

    public CommandContext build() {
        return new CommandContext(arguments, command);
    }
}
