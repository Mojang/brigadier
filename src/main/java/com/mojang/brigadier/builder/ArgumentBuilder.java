// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

/**
 * A skeleton implementation of a Builder for {@link CommandNode}s.
 *
 * @param <S> the type of the command source
 * @param <T> the type of the builder. Used to realize an emulated self type
 */
public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S, T>> {
    private final RootCommandNode<S> arguments = new RootCommandNode<>();
    private Command<S> command;
    private Predicate<S> requirement = s -> true;
    private CommandNode<S> target;
    private RedirectModifier<S> modifier = null;
    private boolean forks;

    /**
     * Returns this argument builder.
     * <p>
     * This returns {@code T} and is therefore suitable to allow chained calls to also call methods of the subclass
     * it was invoked on. This is possible as this emulates a "self" type in java, which would always resolve to the
     * type of the object you call it on.
     *
     * @return this object
     */
    protected abstract T getThis();

    /**
     * Builds the given ArgumentBuilder and adds the result as new child node.
     *
     * @param argument the ArgumentBuilder to add
     * @return this object
     * @throws IllegalStateException if {@link #getRedirect()} is set (i.e. not null)
     * @see #then(CommandNode)
     */
    public T then(final ArgumentBuilder<S, ?> argument) {
        if (target != null) {
            throw new IllegalStateException("Cannot add children to a redirected node");
        }
        arguments.addChild(argument.build());
        return getThis();
    }

    /**
     * Adds the given command node as a child.
     *
     * @param argument the command node to add
     * @return this object
     */
    public T then(final CommandNode<S> argument) {
        if (target != null) {
            throw new IllegalStateException("Cannot add children to a redirected node");
        }
        arguments.addChild(argument);
        return getThis();
    }

    /**
     * Returns all registered child command nodes, which are the registered arguments.
     *
     * @return all registered child command nodes, which are the registered arguments
     */
    public Collection<CommandNode<S>> getArguments() {
        return arguments.getChildren();
    }

    /**
     * Sets the command that will be executed by the built command node.
     *
     * @param command the {@link Command} to execute
     * @return this object
     */
    public T executes(final Command<S> command) {
        this.command = command;
        return getThis();
    }

    /**
     * Returns the {@link Command} the built command node will execute.
     *
     * @return the {@link Command} the built command node will execute
     */
    public Command<S> getCommand() {
        return command;
    }

    /**
     * Sets the predicate that must be true for a command source in order to be able to use the built command node.
     *
     * @param requirement the requirement each command source needs to fulfill in order to be able to use the built
     * command node
     * @return this object
     */
    public T requires(final Predicate<S> requirement) {
        this.requirement = requirement;
        return getThis();
    }

    /**
     * Returns the requirement each command source must meet in order to be able to use this command node.
     *
     * @return the requirement each command source must meet in order to be able to use this command node
     */
    public Predicate<S> getRequirement() {
        return requirement;
    }

    /**
     * Redirects this command node to the target {@link CommandNode}.
     * <p>
     * A redirected node will appear in usage listings, but will otherwise behave just like an alias to the command node
     * it points to.
     * <p>
     * This method sets {@code fork} to false and applies no {@link RedirectModifier}.
     *
     * @param target the command node that will be invoked when the built command node is executed
     * @return this object
     * @see #forward
     */
    public T redirect(final CommandNode<S> target) {
        return forward(target, null, false);
    }

    /**
     * Redirects this command node to the target {@link CommandNode}.
     * <p>
     * A redirected node will appear in usage listings, but will otherwise behave just like an alias to the command
     * node it points to.
     * <p>
     * This method sets {@code fork} to false and applies no {@link RedirectModifier}.
     *
     * @param target the command node that will be invoked when the built command node is executed
     * @return this object
     * @see #forward
     */
    public T redirect(final CommandNode<S> target, final SingleRedirectModifier<S> modifier) {
        return forward(target, modifier == null ? null : o -> Collections.singleton(modifier.apply(o)), false);
    }

    /**
     * Forks this command node, i.e. it splits execution and calls the target command node multiple times with different
     * sources.
     * <p>
     * Forking means that the target command will be invoked multiple times, once for each source in the list of
     * sources generated by the passed {@link RedirectModifier}.
     *
     * @param target the command node that will be invoked when the built command node is executed
     * @param modifier the redirect modifier to use to generate the command source list
     * @return this object
     * @see #forward
     */
    public T fork(final CommandNode<S> target, final RedirectModifier<S> modifier) {
        return forward(target, modifier, true);
    }

    /**
     * Forwards this command in some way to a given target command.
     * <p>
     * It will call the {@code target} command node for each command source in the list the {@code modifier} returns,
     * but the semantics differ slightly. Please have a look at  {@link CommandDispatcher#execute(ParseResults)}
     * for a more detailed explanation.
     *
     * @param target the command node that will be invoked when the built command node is executed
     * @param modifier the redirect modifier to use to generate the command source list
     * @param fork whether the command node should be forked
     * @return this object
     * @see CommandDispatcher#execute(ParseResults)
     */
    public T forward(final CommandNode<S> target, final RedirectModifier<S> modifier, final boolean fork) {
        if (!arguments.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot forward a node with children");
        }
        this.target = target;
        this.modifier = modifier;
        this.forks = fork;
        return getThis();
    }

    /**
     * Returns the command node that the built command node redirects to.
     *
     * @return the command node that the built command node redirects to or null if not set
     */
    public CommandNode<S> getRedirect() {
        return target;
    }

    /**
     * Returns the redirect modifier for the built command node.
     *
     * @return the redirect modifier for the built command node or null if none
     */
    public RedirectModifier<S> getRedirectModifier() {
        return modifier;
    }

    /**
     * Returns whether the built command node forks.
     *
     * @return true if the built command node forks
     */
    public boolean isFork() {
        return forks;
    }

    /**
     * Builds the {@link CommandNode} based on this builder.
     *
     * @return the built command node
     */
    public abstract CommandNode<S> build();
}
