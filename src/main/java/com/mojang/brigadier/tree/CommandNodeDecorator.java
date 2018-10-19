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

abstract class CommandNodeDecorator<S> implements CommandNodeInterface<S> {
    private final CommandNodeInterface<S> delegate;

    public CommandNodeDecorator(final CommandNodeInterface<S> delegate) {
        if(delegate == null) throw new NullPointerException("Delegated object cannot be a null pointer");
        this.delegate = delegate;
    }

    public CommandNodeInterface<S> getDelegate() {
        return delegate;
    }

    @Override
    public Command<S> getCommand() {
        return delegate.getCommand();
    }

    @Override
    public Collection<CommandNodeInterface<S>> getChildren() {
        return delegate.getChildren();
    }

    @Override
    public CommandNodeInterface<S> getChild(final String name) {
        return delegate.getChild(name);
    }

    @Override
    public CommandNodeInterface<S> getRedirect() {
        return delegate.getRedirect();
    }

    @Override
    public RedirectModifier<S> getRedirectModifier() {
        return delegate.getRedirectModifier();
    }

    @Override
    public DefaultCommandNodeDecorator<S, ?> getDefaultNode() {
        return delegate.getDefaultNode();
    }

    @Override
    public boolean canUse(final S source) {
        return delegate.canUse(source);
    }

    @Override
    public void addChild(final CommandNodeInterface<S> node) {
        delegate.addChild(node);
    }

    @Override
    public void findAmbiguities(final AmbiguityConsumer<S> consumer) {
        delegate.findAmbiguities(consumer);
    }

    @Override
    public boolean isValidInput(final String input) {
        return delegate.isValidInput(input);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (this.getClass() != o.getClass()) return false;

        return delegate.equals(((CommandNodeDecorator<?>)o).delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public Predicate<S> getRequirement() {
        return delegate.getRequirement();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getUsageText() {
        return delegate.getUsageText();
    }

    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        delegate.parse(reader, contextBuilder);
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
        return delegate.listSuggestions(context, builder);
    }

    @Override
    public ArgumentBuilderInterface<S, ?> createBuilder() {
        return delegate.createBuilder();
    }

    @Override
    public Collection<? extends CommandNodeInterface<S>> getRelevantNodes(final StringReader input) {
        return delegate.getRelevantNodes(input);
    }

    @Override
    public int compareTo(final CommandNodeInterface<S> o) {
        return delegate.compareTo(o);
    }

    @Override
    public boolean isFork() {
        return delegate.isFork();
    }

    @Override
    public Collection<String> getExamples() {
        return delegate.getExamples();
    }
}
