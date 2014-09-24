package net.minecraft.commands.context;

import com.google.common.collect.Maps;

import java.util.Map;

public class CommandContextBuilder {
    private final Map<String, ParsedArgument<?>> arguments = Maps.newHashMap();

    public CommandContextBuilder() {
    }

    public CommandContextBuilder withArgument(String name, ParsedArgument<?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public CommandContext build() {
        return new CommandContext(arguments);
    }
}
