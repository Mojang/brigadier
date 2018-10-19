// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilderInterface;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public interface CommandNodeInterface<S> extends Comparable<CommandNodeInterface<S>> {
    Command<S> getCommand();

    Collection<CommandNodeInterface<S>> getChildren();

    CommandNodeInterface<S> getChild(String name);

    CommandNodeInterface<S> getRedirect();

    RedirectModifier<S> getRedirectModifier();

    DefaultCommandNodeDecorator<S, ?> getDefaultNode();

    boolean canUse(S source);

    void addChild(CommandNodeInterface<S> node);

    void findAmbiguities(AmbiguityConsumer<S> consumer);

    boolean isValidInput(String input);

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    Predicate<S> getRequirement();

    String getName();

    String getUsageText();

    void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException;

    CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException;

    ArgumentBuilderInterface<S, ?> createBuilder();

    Collection<? extends CommandNodeInterface<S>> getRelevantNodes(StringReader input);

    @Override
    default int compareTo(final CommandNodeInterface<S> o) {
        final CommandNodeInterface<S> that = getUndecoratedNode();
        final CommandNodeInterface<S> other = o.getUndecoratedNode();

        if (that instanceof CommandNode && other instanceof CommandNode && this instanceof LiteralCommandNode == o instanceof LiteralCommandNode) {
            return ((CommandNode<S>)that).getSortedKey().compareTo(((CommandNode<?>)other).getSortedKey());
        }

        return (other instanceof LiteralCommandNode) ? 1 : -1;
    }

    boolean isFork();

    Collection<String> getExamples();

    default CommandNodeInterface<S> getUndecoratedNode() {
        CommandNodeInterface<S> result = this;

        while(result instanceof CommandNodeDecorator)
            result = ((CommandNodeDecorator<S>)result).getDelegate();

        return result;
    }
}
