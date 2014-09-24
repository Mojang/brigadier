package net.minecraft.commands.context;

import com.google.common.collect.Maps;
import net.minecraft.commands.arguments.CommandArgumentType;

import java.util.Map;

public class CommandContextBuilder {
    private final Map<String, CommandArgumentType.CommandArgumentParseResult<?>> arguments = Maps.newHashMap();

    public CommandContextBuilder() {
    }

    public CommandContextBuilder withArgument(String name, CommandArgumentType.CommandArgumentParseResult<?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public CommandContext build() {
        return new CommandContext(arguments);
    }
}
