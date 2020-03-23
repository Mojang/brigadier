// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.tree.CommandNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandContextBuilder<S, R> {
    private final Map<String, ParsedArgument<S, ?>> arguments = new LinkedHashMap<>();
    private final CommandNode<S, R> rootNode;
    private final List<ParsedCommandNode<S, R>> nodes = new ArrayList<>();
    private final CommandDispatcher<S, R> dispatcher;
    private S source;
    private Command<S, R> command;
    private CommandContextBuilder<S, R> child;
    private StringRange range;
    private RedirectModifier<S, R> modifier = null;
    private boolean forks;

    public CommandContextBuilder(final CommandDispatcher<S, R> dispatcher, final S source, final CommandNode<S, R> rootNode, final int start) {
        this.rootNode = rootNode;
        this.dispatcher = dispatcher;
        this.source = source;
        this.range = StringRange.at(start);
    }

    public CommandContextBuilder<S, R> withSource(final S source) {
        this.source = source;
        return this;
    }

    public S getSource() {
        return source;
    }

    public CommandNode<S, R> getRootNode() {
        return rootNode;
    }

    public CommandContextBuilder<S, R> withArgument(final String name, final ParsedArgument<S, ?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public Map<String, ParsedArgument<S, ?>> getArguments() {
        return arguments;
    }

    public CommandContextBuilder<S, R> withCommand(final Command<S, R> command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder<S, R> withNode(final CommandNode<S, R> node, final StringRange range) {
        nodes.add(new ParsedCommandNode<>(node, range));
        this.range = StringRange.encompassing(this.range, range);
        this.modifier = node.getRedirectModifier();
        this.forks = node.isFork();
        return this;
    }

    public CommandContextBuilder<S, R> copy() {
        final CommandContextBuilder<S, R> copy = new CommandContextBuilder<>(dispatcher, source, rootNode, range.getStart());
        copy.command = command;
        copy.arguments.putAll(arguments);
        copy.nodes.addAll(nodes);
        copy.child = child;
        copy.range = range;
        copy.forks = forks;
        return copy;
    }

    public CommandContextBuilder<S, R> withChild(final CommandContextBuilder<S, R> child) {
        this.child = child;
        return this;
    }

    public CommandContextBuilder<S, R> getChild() {
        return child;
    }

    public CommandContextBuilder<S, R> getLastChild() {
        CommandContextBuilder<S, R> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    public Command<S, R> getCommand() {
        return command;
    }

    public List<ParsedCommandNode<S, R>> getNodes() {
        return nodes;
    }

    public CommandContext<S, R> build(final String input) {
        return new CommandContext<>(source, input, arguments, command, rootNode, nodes, range, child == null ? null : child.build(input), modifier, forks);
    }

    public CommandDispatcher<S, R> getDispatcher() {
        return dispatcher;
    }

    public StringRange getRange() {
        return range;
    }

    public SuggestionContext<S, R> findSuggestionContext(final int cursor) {
        if (range.getStart() <= cursor) {
            if (range.getEnd() < cursor) {
                if (child != null) {
                    return child.findSuggestionContext(cursor);
                } else if (!nodes.isEmpty()) {
                    final ParsedCommandNode<S, R> last = nodes.get(nodes.size() - 1);
                    return new SuggestionContext<>(last.getNode(), last.getRange().getEnd() + 1);
                } else {
                    return new SuggestionContext<>(rootNode, range.getStart());
                }
            } else {
                CommandNode<S, R> prev = rootNode;
                for (final ParsedCommandNode<S, R> node : nodes) {
                    final StringRange nodeRange = node.getRange();
                    if (nodeRange.getStart() <= cursor && cursor <= nodeRange.getEnd()) {
                        return new SuggestionContext<>(prev, nodeRange.getStart());
                    }
                    prev = node.getNode();
                }
                if (prev == null) {
                    throw new IllegalStateException("Can't find node before cursor");
                }
                return new SuggestionContext<>(prev, range.getStart());
            }
        }
        throw new IllegalStateException("Can't find node before cursor");
    }
}
