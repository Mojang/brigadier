// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.CommandNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A general container class storing information needed to invoke a command.
 * <p>
 * This consists of e.g. the command source to invoke it for, the command to invoke, child contexts (for subcommands)
 * or arguments parsed by {@link ArgumentType}s.
 *
 * @param <S> the type of the command source
 */
public class CommandContext<S> {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();

    static {
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
    }

    private final S source;
    private final String input;
    private final Command<S> command;
    private final Map<String, ParsedArgument<S, ?>> arguments;
    private final CommandNode<S> rootNode;
    private final List<ParsedCommandNode<S>> nodes;
    private final StringRange range;
    private final CommandContext<S> child;
    private final RedirectModifier<S> modifier;
    private final boolean forks;

    /**
     * Creates a new {@link CommandContext}.
     *
     * @param source the command source to invoke the command for
     * @param input the full input
     * @param arguments the parsed arguments, as created by {@link ArgumentType}s
     * @param command the command to invoke
     * @param rootNode the root node of the command tree
     * @param nodes the
     * @param range the string range indicating what part in the input this context covers
     * @param child the child context, or null if none
     * @param modifier the {@link RedirectModifier} to apply when invoking the command
     * @param forks whether this command forks. See {@link CommandDispatcher#execute(ParseResults)} for an explanation
     */
    public CommandContext(final S source, final String input, final Map<String, ParsedArgument<S, ?>> arguments, final Command<S> command, final CommandNode<S> rootNode, final List<ParsedCommandNode<S>> nodes, final StringRange range, final CommandContext<S> child, final RedirectModifier<S> modifier, boolean forks) {
        this.source = source;
        this.input = input;
        this.arguments = arguments;
        this.command = command;
        this.rootNode = rootNode;
        this.nodes = nodes;
        this.range = range;
        this.child = child;
        this.modifier = modifier;
        this.forks = forks;
    }

    /**
     * Creates a copy of this {@link CommandContext} that for a different command source but otherwise identical
     *
     * @param source the command source to copy it for
     * @return a {@link CommandContext} that is identical to this one, except for the command source
     */
    public CommandContext<S> copyFor(final S source) {
        if (this.source == source) {
            return this;
        }
        return new CommandContext<>(source, input, arguments, command, rootNode, nodes, range, child, modifier, forks);
    }

    /**
     * Returns the child context, if it is present.
     *
     * @return the child context, or null if there is none
     */
    public CommandContext<S> getChild() {
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
     */
    public CommandContext<S> getLastChild() {
        CommandContext<S> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    /**
     * Returns the command that should be executed.
     *
     * @return the command to execute or null if not found
     */
    public Command<S> getCommand() {
        return command;
    }

    /**
     * The command source to invoke the command for
     *
     * @return the command source to invoke the command for
     */
    public S getSource() {
        return source;
    }

    /**
     * Returns an argument that was stored in this context while parsing the command.
     *
     * @param name the name of the argument to retrieve
     * @param clazz the class of the argument type
     * @param <V> the type of the argument
     * @return the argument
     * @throws IllegalArgumentException if the command does not exist or is of a different type
     */
    public <V> V getArgument(final String name, final Class<V> clazz) {
        final ParsedArgument<S, ?> argument = arguments.get(name);

        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        }

        final Object result = argument.getResult();
        if (PRIMITIVE_TO_WRAPPER.getOrDefault(clazz, clazz).isAssignableFrom(result.getClass())) {
            @SuppressWarnings("unchecked")
            V v = (V) result;
            return v;
        } else {
            throw new IllegalArgumentException("Argument '" + name + "' is defined as " + result.getClass().getSimpleName() + ", not " + clazz);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandContext)) return false;

        final CommandContext that = (CommandContext) o;

        if (!arguments.equals(that.arguments)) return false;
        if (!rootNode.equals(that.rootNode)) return false;
        if (nodes.size() != that.nodes.size() || !nodes.equals(that.nodes)) return false;
        if (command != null ? !command.equals(that.command) : that.command != null) return false;
        if (!source.equals(that.source)) return false;
        if (child != null ? !child.equals(that.child) : that.child != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + arguments.hashCode();
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + rootNode.hashCode();
        result = 31 * result + nodes.hashCode();
        result = 31 * result + (child != null ? child.hashCode() : 0);
        return result;
    }

    /**
     * Returns the {@link RedirectModifier} to apply when invoking the command.
     *
     * @return the  {@link RedirectModifier} to apply when invoking the command or null if none is set
     */
    public RedirectModifier<S> getRedirectModifier() {
        return modifier;
    }

    /**
     * Returns the range this context takes up in the input string.
     *
     * @return the range this context takes up in the input string.
     */
    public StringRange getRange() {
        return range;
    }

    /**
     * Returns the full input, of which this command context is a part.
     *
     * @return the full input, of which this command context is a part.
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns the root command node in the command tree.
     *
     * @return the root command node in the command tree
     */
    public CommandNode<S> getRootNode() {
        return rootNode;
    }

    /**
     * Returns all nodes associated with this context.
     * <p>
     * That is the node that {@link #getCommand()} comes from any anything else that nodes pushes onto it in its
     * {@link CommandNode#parse} method.
     * <p>
     * TODO: Why is this a List?
     *
     * @return all nodes associated with this context
     */
    public List<ParsedCommandNode<S>> getNodes() {
        return nodes;
    }

    /**
     * Returns true if this context has any {@link CommandNode} associated with it.
     *
     * @return true if this context has any {@link CommandNode} associated with it
     */
    public boolean hasNodes() {
        return !nodes.isEmpty();
    }

    /**
     * Returns true if this command is forked.
     * <p>
     * See {@link CommandDispatcher#execute(ParseResults)} for a detailed explanation.
     *
     * @return true if this command is forked
     * @see CommandDispatcher#execute(ParseResults)
     */
    public boolean isForked() {
        return forks;
    }
}
