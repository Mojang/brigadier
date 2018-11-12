// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The abstract base for the command tree, just representing a single command node.
 *
 * @param <S> the type of the command source
 */
public abstract class CommandNode<S> implements Comparable<CommandNode<S>> {
    private Map<String, CommandNode<S>> children = new LinkedHashMap<>();
    private Map<String, LiteralCommandNode<S>> literals = new LinkedHashMap<>();
    private Map<String, ArgumentCommandNode<S, ?>> arguments = new LinkedHashMap<>();
    private final Predicate<S> requirement;
    private final CommandNode<S> redirect;
    private final RedirectModifier<S> modifier;
    private final boolean forks;
    private Command<S> command;

    protected CommandNode(final Command<S> command, final Predicate<S> requirement, final CommandNode<S> redirect, final RedirectModifier<S> modifier, final boolean forks) {
        this.command = command;
        this.requirement = requirement;
        this.redirect = redirect;
        this.modifier = modifier;
        this.forks = forks;
    }

    /**
     * Returns the command to execute when executing this command.
     *
     * @return the command to execute when executing this command
     */
    public Command<S> getCommand() {
        return command;
    }

    /**
     * Returns all child commands.
     *
     * @return all child commands
     */
    public Collection<CommandNode<S>> getChildren() {
        return children.values();
    }

    /**
     * Returns a child with the given {@link #getName}.
     *
     * @param name the name of the child command
     * @return the child with that name or null if not found
     */
    public CommandNode<S> getChild(final String name) {
        return children.get(name);
    }

    /**
     * Returns the command node to redirect to.
     *
     * @return the command node to redirect to or null if none
     */
    public CommandNode<S> getRedirect() {
        return redirect;
    }

    /**
     * Returns the redirect modifier to apply when redirecting.
     *
     * @return the redirect modifier to apply when redirecting or null if none
     */
    public RedirectModifier<S> getRedirectModifier() {
        return modifier;
    }

    /**
     * Checks whether the given command source can use this command.
     * <p>
     * This just checks the {@link #getRequirement()} predicate, which could e.g. check for permissions.
     *
     * @param source the command source to check for
     * @return true if the given command source can use this command
     */
    public boolean canUse(final S source) {
        return requirement.test(source);
    }

    /**
     * Adds a new child node to this command node.
     * <p>
     * This will replace commands with the same name.
     *
     * <strong>You are not allowed to add children to commands with a {@link #getRedirect()} target!</strong>
     * @param node the child command node to add
     * @throws UnsupportedOperationException if you try to add a {@link RootCommandNode} to any other command
     */
    public void addChild(final CommandNode<S> node) {
        if (node instanceof RootCommandNode) {
            throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
        }

        final CommandNode<S> child = children.get(node.getName());
        if (child != null) {
            // We've found something to merge onto
            if (node.getCommand() != null) {
                child.command = node.getCommand();
            }
            for (final CommandNode<S> grandchild : node.getChildren()) {
                child.addChild(grandchild);
            }
        } else {
            children.put(node.getName(), node);
            if (node instanceof LiteralCommandNode) {
                literals.put(node.getName(), (LiteralCommandNode<S>) node);
            } else if (node instanceof ArgumentCommandNode) {
                arguments.put(node.getName(), (ArgumentCommandNode<S, ?>) node);
            }
        }

        children = children.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Tries to find ambiguities in the children of this command and recurses down.
     * <p>
     * This can be used to detect whether multiple paths could be taken with a single input, which is not an ideal state
     * for parsing commands. See {@link CommandDispatcher#findAmbiguities} for more information.
     *
     * @param consumer the {@link AmbiguityConsumer} to call when ambiguities are found
     */
    public void findAmbiguities(final AmbiguityConsumer<S> consumer) {
        Set<String> matches = new HashSet<>();

        for (final CommandNode<S> child : children.values()) {
            for (final CommandNode<S> sibling : children.values()) {
                if (child == sibling) {
                    continue;
                }

                for (final String input : child.getExamples()) {
                    if (sibling.isValidInput(input)) {
                        matches.add(input);
                    }
                }

                if (matches.size() > 0) {
                    consumer.ambiguous(this, child, sibling, matches);
                    matches = new HashSet<>();
                }
            }

            child.findAmbiguities(consumer);
        }
    }

    /**
     * Checks if the given input is valid for this command, i.e. if it is what the command expects.
     * <p>
     * This is used to find ambiguities.
     *
     * @param input the input to check
     * @return true if the given input is valid
     * @see #findAmbiguities}
     */
    protected abstract boolean isValidInput(final String input);

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandNode)) return false;

        final CommandNode<S> that = (CommandNode<S>) o;

        if (!children.equals(that.children)) return false;
        if (command != null ? !command.equals(that.command) : that.command != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * children.hashCode() + (command != null ? command.hashCode() : 0);
    }

    /**
     * Returns the requirement for this command, that is used by {@link #canUse(Object)}.
     *
     * @return the requirement for this command, that is used by {@link #canUse(Object)}
     */
    public Predicate<S> getRequirement() {
        return requirement;
    }

    /**
     * Returns the name of this command.
     *
     * @return the name of this command
     */
    public abstract String getName();

    /**
     * Returns some usage text for this command.
     *
     * @return some usage text for this command
     */
    public abstract String getUsageText();

    /**
     * Tries to parse the command given by the reader and stores the results in the contextBuilder.
     *
     * @param reader the reader to read from
     * @param contextBuilder the context builder to store the results in
     * @throws CommandSyntaxException if the input was malformed
     */
    public abstract void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException;

    /**
     * Lists suggestions for this command, given the context and uses the passed {@link SuggestionsBuilder} to build
     * them.
     *
     * @param context the {@link CommandContext} to use for finding suggestions
     * @param builder the suggestions builder
     * @return a completable future that might complete sometime in the future, as finding suggestions could involve
     * I/O or other slow things
     * @throws CommandSyntaxException if the command is not well formed
     */
    public abstract CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException;

    /**
     * Creates a builder for this command.
     *
     * @return a builder for this command
     */
    public abstract ArgumentBuilder<S, ?> createBuilder();

    /**
     * Returns a key for this command, that is used for sorting the commands
     *
     * @return a key for this command, that is used for sorting the output
     */
    protected abstract String getSortedKey();

    /**
     * Returns all relevant nodes for the input, so all nodes that are probably able to parse the input.
     *
     * @param input the input to check for
     * @return all relevant nodes for the input, so all nodes that are probably able to parse the input
     */
    public Collection<? extends CommandNode<S>> getRelevantNodes(final StringReader input) {
        if (literals.size() > 0) {
            final int cursor = input.getCursor();
            while (input.canRead() && input.peek() != ' ') {
                input.skip();
            }
            final String text = input.getString().substring(cursor, input.getCursor());
            input.setCursor(cursor);
            final LiteralCommandNode<S> literal = literals.get(text);
            if (literal != null) {
                return Collections.singleton(literal);
            } else {
                return arguments.values();
            }
        } else {
            return arguments.values();
        }
    }

    @Override
    public int compareTo(final CommandNode<S> o) {
        if (this instanceof LiteralCommandNode == o instanceof LiteralCommandNode) {
            return getSortedKey().compareTo(o.getSortedKey());
        }

        return (o instanceof LiteralCommandNode) ? 1 : -1;
    }

    /**
     * Checks whether this command forks.
     * <p>
     * See {@link CommandDispatcher#execute(ParseResults)} for an explanation of what it does
     *
     * @return true if this command forks
     */
    public boolean isFork() {
        return forks;
    }

    /**
     * Returns example usages for this command, which are used to find ambiguities.
     *
     * @return some example usages for this command, which are used to find ambiguities
     * @see #findAmbiguities
     */
    public abstract Collection<String> getExamples();
}
