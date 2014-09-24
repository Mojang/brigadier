package net.minecraft.commands.context;

import com.google.common.primitives.Primitives;
import net.minecraft.commands.Command;

import java.util.Map;

public class CommandContext {
    private final Map<String, ParsedArgument<?>> arguments;
    private final Command command;

    public CommandContext(Map<String, ParsedArgument<?>> arguments, Command command) {
        this.arguments = arguments;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    @SuppressWarnings("unchecked")
    public <T> ParsedArgument<T> getArgument(String name, Class<T> clazz) {
        ParsedArgument<?> argument = arguments.get(name);

        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        }

        if (Primitives.wrap(clazz).isAssignableFrom(argument.getResult().getClass())) {
            return (ParsedArgument<T>) argument;
        } else {
            throw new IllegalArgumentException("Argument '" + name + "' is defined as " + argument.getResult().getClass().getSimpleName() + ", not " + clazz);
        }
    }
}
