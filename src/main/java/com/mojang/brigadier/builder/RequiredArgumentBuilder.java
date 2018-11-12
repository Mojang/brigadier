// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

/**
 * An {@link ArgumentBuilder} for a {@link ArgumentCommandNode} that is triggered by an argument, like a number.
 * <p>
 * The type of the argument is set via an {@link ArgumentType}, which takes care of parsing the stringified argument
 * passed by users.
 * <p>
 * <br>A short <strong>example</strong>:
 * <br>A subcommand for a delete command, taking the number of messages to delete. The {@link ArgumentType} would be an
 * {@link IntegerArgumentType} then and the built command would parse things like {@literal "20"} or {@literal "-230"}.
 *
 * @param <S> the type of the command source
 * @param <T> the {@link ArgumentType} the built command will use
 */
public class RequiredArgumentBuilder<S, T> extends ArgumentBuilder<S, RequiredArgumentBuilder<S, T>> {
    private final String name;
    private final ArgumentType<T> type;
    private SuggestionProvider<S> suggestionsProvider = null;

    private RequiredArgumentBuilder(final String name, final ArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * A factory method to create a new builder for an argument command node.
     * <p>
     * This method is intended for static importing, so you can just use {@code argument(<name>, <type>)"} in your code.
     *
     * @param name the name of the argument. Used with {@link CommandContext#getArgument} to retrieve the parsed
     * argument out of a {@link CommandContext}
     * @param type the {@link ArgumentType} this command takes
     * @param <S> the type of the command source
     * @param <T> the java type of the argument this command takes
     * @return a {@link RequiredArgumentBuilder} with a given name and argument type
     */
    public static <S, T> RequiredArgumentBuilder<S, T> argument(final String name, final ArgumentType<T> type) {
        return new RequiredArgumentBuilder<>(name, type);
    }

    /**
     * Sets the {@link SuggestionProvider} that provides the user with suggestions about what the next value could be.
     *
     * @param provider the {@link SuggestionProvider} to use
     * @return this argument builder
     */
    public RequiredArgumentBuilder<S, T> suggests(final SuggestionProvider<S> provider) {
        this.suggestionsProvider = provider;
        return getThis();
    }

    /**
     * Returns the {@link SuggestionProvider} the command uses to advice the user on possible completions.
     *
     * @return the registered {@link SuggestionProvider} or null if not set
     */
    public SuggestionProvider<S> getSuggestionsProvider() {
        return suggestionsProvider;
    }

    @Override
    protected RequiredArgumentBuilder<S, T> getThis() {
        return this;
    }

    /**
     * Returns the {@link ArgumentType} the built command will use.
     *
     * @return the {@link ArgumentType} the built command will use
     */
    public ArgumentType<T> getType() {
        return type;
    }

    /**
     * Returns the name of the argument the built command will have.
     * <p>
     * Can be used with {@link CommandContext#getArgument} to retrieve the parsed argument
     *
     * @return the name of the argument the built command will have
     */
    public String getName() {
        return name;
    }

    @Override
    public ArgumentCommandNode<S, T> build() {
        final ArgumentCommandNode<S, T> result = new ArgumentCommandNode<>(getName(), getType(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getSuggestionsProvider());

        for (final CommandNode<S> argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
