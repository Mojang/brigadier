// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.tree.CommandNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public CommandContext<S> copyFor(final S source) {
        if (this.source == source) {
            return this;
        }
        return new CommandContext<>(source, input, arguments, command, rootNode, nodes, range, child, modifier, forks);
    }

    public CommandContext<S> getChild() {
        return child;
    }

    public CommandContext<S> getLastChild() {
        CommandContext<S> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    public Command<S> getCommand() {
        return command;
    }

    public S getSource() {
        return source;
    }

    @SuppressWarnings("unchecked")
    public <V> V getArgument(final String name, final Class<V> clazz) {
        final ParsedArgument<S, ?> argument = arguments.get(name);

        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        }

        final Object result = argument.getResult();
        if (PRIMITIVE_TO_WRAPPER.getOrDefault(clazz, clazz).isAssignableFrom(result.getClass())) {
            return (V) result;
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

    public RedirectModifier<S> getRedirectModifier() {
        return modifier;
    }

    public StringRange getRange() {
        return range;
    }

    public String getInput() {
        return input;
    }

    public CommandNode<S> getRootNode() {
        return rootNode;
    }

    public List<ParsedCommandNode<S>> getNodes() {
        return nodes;
    }

    public boolean hasNodes() {
        return !nodes.isEmpty();
    }

    public boolean isForked() {
        return forks;
    }
}
