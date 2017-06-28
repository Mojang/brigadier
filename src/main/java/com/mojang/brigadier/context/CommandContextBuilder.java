package com.mojang.brigadier.context;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Map;

public class CommandContextBuilder<S> {
    private final Map<String, ParsedArgument<?>> arguments = Maps.newHashMap();
    private final Map<CommandNode<S>, String> nodes = Maps.newLinkedHashMap();
    private final S source;
    private Command<S> command;

    public CommandContextBuilder(S source) {
        this.source = source;
    }

    public S getSource() {
        return source;
    }

    public CommandContextBuilder<S> withArgument(String name, ParsedArgument<?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public Map<String, ParsedArgument<?>> getArguments() {
        return arguments;
    }

    public CommandContextBuilder<S> withCommand(Command<S> command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder<S> withNode(CommandNode<S> node, String raw) {
        this.nodes.put(node, raw);
        return this;
    }

    public CommandContextBuilder<S> copy() {
        CommandContextBuilder<S> copy = new CommandContextBuilder<>(source);
        copy.command = this.command;
        this.arguments.forEach((k, v) -> copy.arguments.put(k, v.copy()));
        copy.nodes.putAll(this.nodes);
        return copy;
    }

    public CommandContext<S> build() {
        return new CommandContext<>(source, arguments, command, nodes);
    }
}
