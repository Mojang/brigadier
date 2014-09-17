package net.minecraft.commands.builder;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class ArgumentBuilder {
    private final List<ArgumentBuilder> arguments = Lists.newArrayList();

    public ArgumentBuilder then(ArgumentBuilder argument) {
        arguments.add(argument);
        return this;
    }

    public List<ArgumentBuilder> getArguments() {
        return arguments;
    }
}
