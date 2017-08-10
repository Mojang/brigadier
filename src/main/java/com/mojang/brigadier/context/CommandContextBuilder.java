package com.mojang.brigadier.context;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Map;

public class CommandContextBuilder<S> {
    private final Map<String, ParsedArgument<S, ?>> arguments = Maps.newLinkedHashMap();
    private final Map<CommandNode<S>, String> nodes = Maps.newLinkedHashMap();
    private final CommandDispatcher<S> dispatcher;
    private S source;
    private Command<S> command;
    private CommandContext<S> parent;

    public CommandContextBuilder(final CommandDispatcher<S> dispatcher, final S source) {
        this.dispatcher = dispatcher;
        this.source = source;
    }

    public CommandContextBuilder<S> withSource(final S source) {
        this.source = source;
        return this;
    }

    public S getSource() {
        return source;
    }

    public CommandContextBuilder<S> withArgument(final String name, final ParsedArgument<S, ?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public Map<String, ParsedArgument<S, ?>> getArguments() {
        return arguments;
    }

    public CommandContextBuilder<S> withCommand(final Command<S> command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder<S> withNode(final CommandNode<S> node, final String raw) {
        nodes.put(node, raw);
        return this;
    }

    public CommandContextBuilder<S> copy() {
        final CommandContextBuilder<S> copy = new CommandContextBuilder<>(dispatcher, source);
        copy.command = command;
        copy.arguments.putAll(arguments);
        copy.nodes.putAll(nodes);
        copy.parent = parent;
        return copy;
    }

    public CommandContextBuilder<S> redirect(final CommandNode<S> newRoot) {
        final CommandContextBuilder<S> result = new CommandContextBuilder<>(dispatcher, source);
        result.withNode(newRoot, "");
        result.parent = build();
        return result;
    }

    public CommandContext<S> getParent() {
        return parent;
    }

    public String getInput() {
        final StringBuilder result = new StringBuilder();
        boolean first = true;
        for (final String node : nodes.values()) {
            if (first) {
                if (!node.isEmpty()) {
                    first = false;
                }
            } else {
                result.append(CommandDispatcher.ARGUMENT_SEPARATOR);
            }
            result.append(node);
        }
        return result.toString();
    }

    public Map<CommandNode<S>, String> getNodes() {
        return nodes;
    }

    public CommandContext<S> build() {
        return new CommandContext<>(source, arguments, command, nodes, getInput(), parent);
    }

    public CommandDispatcher<S> getDispatcher() {
        return dispatcher;
    }
}
