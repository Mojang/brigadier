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

/**
 * A builder for {@link CommandContext} objects.
 *
 * @param <S> the type of the command source
 */
public class CommandContextBuilder<S> {
    private final Map<String, ParsedArgument<S, ?>> arguments = new LinkedHashMap<>();
    private final CommandNode<S> rootNode;
    private final List<ParsedCommandNode<S>> nodes = new ArrayList<>();
    private final CommandDispatcher<S> dispatcher;
    private S source;
    private Command<S> command;
    private CommandContextBuilder<S> child;
    private StringRange range;
    private RedirectModifier<S> modifier = null;
    private boolean forks;

    /**
     * Creates a new {@link CommandContextBuilder} with a few required arguments.
     *
     * @param dispatcher the {@link CommandDispatcher} TODO: Why does this exist here?
     * @param source the command source
     * @param rootNode the root node of the command tree
     * @param start the start in the input that this context spans
     */
    public CommandContextBuilder(final CommandDispatcher<S> dispatcher, final S source, final CommandNode<S> rootNode, final int start) {
        this.rootNode = rootNode;
        this.dispatcher = dispatcher;
        this.source = source;
        this.range = StringRange.at(start);
    }

    /**
     * Sets the source for this command.
     *
     * @param source thr command source
     * @return this builder
     */
    public CommandContextBuilder<S> withSource(final S source) {
        this.source = source;
        return this;
    }

    /**
     * The command source for the built command context.
     *
     * @return the command source for the built command context
     */
    public S getSource() {
        return source;
    }

    /**
     * Returns the root node in the command tree.
     *
     * @return the root node in the command tree
     */
    public CommandNode<S> getRootNode() {
        return rootNode;
    }

    /**
     * Stores an argument in this command, that can later be retrieved.
     *
     * @param name the name of the argument
     * @param argument the argument to store
     * @return this builder
     * @see CommandContext#getArgument
     */
    public CommandContextBuilder<S> withArgument(final String name, final ParsedArgument<S, ?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    /**
     * Returns all stored arguments.
     *
     * @return all stored arguments
     */
    public Map<String, ParsedArgument<S, ?>> getArguments() {
        return arguments;
    }

    /**
     * Sets the command to execute, i.e. the one that was matched by this context.
     *
     * @param command the {@link Command} that should be executed
     * @return this builder
     */
    public CommandContextBuilder<S> withCommand(final Command<S> command) {
        this.command = command;
        return this;
    }

    /**
     * Adds the given command node alongside its parsed range to this context.
     * <p>
     * <strong>You should probably not call this method in your code, as duplicate invocations could put the
     * builder into an invalid state.</strong>
     *
     * @param node the command node to add
     * @param range the range the node spans in the input
     * @return this builder
     */
    public CommandContextBuilder<S> withNode(final CommandNode<S> node, final StringRange range) {
        nodes.add(new ParsedCommandNode<>(node, range));
        this.range = StringRange.encompassing(this.range, range);
        this.modifier = node.getRedirectModifier();
        this.forks = node.isFork();
        return this;
    }

    /**
     * Creates a copy of this {@link CommandContextBuilder}.
     * <p>
     * Not a deep copy as Command, child, range and the arguments themselves are shared.
     *
     * @return a copy of this builder
     */
    public CommandContextBuilder<S> copy() {
        final CommandContextBuilder<S> copy = new CommandContextBuilder<>(dispatcher, source, rootNode, range.getStart());
        copy.command = command;
        copy.arguments.putAll(arguments);
        copy.nodes.addAll(nodes);
        copy.child = child;
        copy.range = range;
        copy.forks = forks;
        return copy;
    }

    /**
     * Sets a child context, which is the context for a sub command.
     *
     * @param child the child context
     * @return this builder
     */
    public CommandContextBuilder<S> withChild(final CommandContextBuilder<S> child) {
        this.child = child;
        return this;
    }

    /**
     * Returns the child context, i.e. the context for a subcommand.
     *
     * @return the child context, i.e. the context for a subcommand
     */
    public CommandContextBuilder<S> getChild() {
        return child;
    }

    /**
     * Returns the last child command context in the chain, i.e. the lowest child you can reach from this context.
     * <p>
     * As each {@link CommandContext} can have child, you can have a child of a child. This method returns the lowest
     * possible child you can reach, i.e. the last command context that has no children.
     * This can be this command context instance, if it has no child .
     *
     * @return the last child command context
     * @see CommandContext#getLastChild()
     */
    public CommandContextBuilder<S> getLastChild() {
        CommandContextBuilder<S> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    /**
     * Returns the command to execute.
     *
     * @return the command to execute
     */
    public Command<S> getCommand() {
        return command;
    }

    /**
     * Returns a list with all nodes.
     *
     * @return a list with all nodes
     * @see CommandContext#getNodes
     */
    public List<ParsedCommandNode<S>> getNodes() {
        return nodes;
    }

    /**
     * Builds the command context
     *
     * @param input the input string
     * @return the built command context
     */
    public CommandContext<S> build(final String input) {
        return new CommandContext<>(source, input, arguments, command, rootNode, nodes, range, child == null ? null : child.build(input), modifier, forks);
    }

    /**
     * Returns the {@link CommandDispatcher} set in the constructor.
     * TODO: WHY?
     *
     * @return the {@link CommandDispatcher} set in the constructor
     */
    public CommandDispatcher<S> getDispatcher() {
        return dispatcher;
    }

    /**
     * Returns the range this context spans in the input.
     *
     * @return the range this context spans in the input
     */
    public StringRange getRange() {
        return range;
    }

    /**
     * Finds the {@link SuggestionContext} given a cursor value.
     * <p>
     * This method attempts to find the correct command node responsible at the given cursor position.
     *
     * @param cursor the cursor position to find a suggestion context for
     * @return the {@link SuggestionContext} for the given cursor value
     * @throws IllegalStateException if the cursor position is smaller than the {@link #getRange()} or not within the
     * bounds of any registered node
     */
    public SuggestionContext<S> findSuggestionContext(final int cursor) {
        if (range.getStart() <= cursor) {
            if (range.getEnd() < cursor) {
                if (child != null) {
                    return child.findSuggestionContext(cursor);
                } else if (!nodes.isEmpty()) {
                    final ParsedCommandNode<S> last = nodes.get(nodes.size() - 1);
                    return new SuggestionContext<>(last.getNode(), last.getRange().getEnd() + 1);
                } else {
                    return new SuggestionContext<>(rootNode, range.getStart());
                }
            } else {
                CommandNode<S> prev = rootNode;
                for (final ParsedCommandNode<S> node : nodes) {
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
