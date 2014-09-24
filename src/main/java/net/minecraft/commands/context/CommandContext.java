package net.minecraft.commands.context;

import com.google.common.primitives.Primitives;

import java.util.Map;

public class CommandContext {
    private final Map<String, ParsedArgument<?>> arguments;

    public CommandContext(Map<String, ParsedArgument<?>> arguments) {
        this.arguments = arguments;
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
