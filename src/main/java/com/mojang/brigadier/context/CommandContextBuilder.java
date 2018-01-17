package com.mojang.brigadier.context;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Map;

public class CommandContextBuilder<S> {
    private final Map<String, ParsedArgument<S, ?>> arguments = Maps.newLinkedHashMap();
    private final Map<CommandNode<S>, StringRange> nodes = Maps.newLinkedHashMap();
    private final CommandDispatcher<S> dispatcher;
    private S source;
    private Command<S> command;
    private CommandContextBuilder<S> child;
    private StringRange range;
    private RedirectModifier<S> modifier = null;
    private boolean forks;

    public CommandContextBuilder(final CommandDispatcher<S> dispatcher, final S source, final int start) {
        this.dispatcher = dispatcher;
        this.source = source;
        this.range = StringRange.at(start);
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

    public CommandContextBuilder<S> withNode(final CommandNode<S> node, final StringRange range) {
        nodes.put(node, range);
        this.range = StringRange.encompassing(this.range, range);
        this.modifier = node.getRedirectModifier();
        this.forks = node.isFork();
        return this;
    }

    public CommandContextBuilder<S> copy() {
        final CommandContextBuilder<S> copy = new CommandContextBuilder<>(dispatcher, source, range.getStart());
        copy.command = command;
        copy.arguments.putAll(arguments);
        copy.nodes.putAll(nodes);
        copy.child = child;
        copy.range = range;
        copy.forks = forks;
        return copy;
    }

    public CommandContextBuilder<S> withChild(final CommandContextBuilder<S> child) {
        this.child = child;
        return this;
    }

    public CommandContextBuilder<S> getChild() {
        return child;
    }

    public CommandContextBuilder<S> getLastChild() {
        CommandContextBuilder<S> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    public Command<S> getCommand() {
        return command;
    }

    public Map<CommandNode<S>, StringRange> getNodes() {
        return nodes;
    }

    public CommandContext<S> build(final String input) {
        return new CommandContext<>(source, input, arguments, command, nodes, range, child == null ? null : child.build(input), modifier, forks);
    }

    public CommandDispatcher<S> getDispatcher() {
        return dispatcher;
    }

    public StringRange getRange() {
        return range;
    }
}
