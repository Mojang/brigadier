package net.minecraft.commands.context;

import com.google.common.primitives.Primitives;
import net.minecraft.commands.arguments.CommandArgumentType;

import java.util.Map;

public class CommandContext {
    private final Map<String, CommandArgumentType.CommandArgumentParseResult<?>> arguments;

    public CommandContext(Map<String, CommandArgumentType.CommandArgumentParseResult<?>> arguments) {
        this.arguments = arguments;
    }

    @SuppressWarnings("unchecked")
    public <T> CommandArgumentType.CommandArgumentParseResult<T> getArgument(String name, Class<T> clazz) {
        CommandArgumentType.CommandArgumentParseResult<?> argument = arguments.get(name);

        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        }

        if (Primitives.wrap(clazz).isAssignableFrom(argument.getResult().getClass())) {
            return (CommandArgumentType.CommandArgumentParseResult<T>) argument;
        } else {
            throw new IllegalArgumentException("Argument '" + name + "' is defined as " + argument.getResult().getClass().getSimpleName() + ", not " + clazz);
        }
    }
}
