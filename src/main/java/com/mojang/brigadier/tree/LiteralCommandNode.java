// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class LiteralCommandNode<S> extends CommandNode<S> {
    private final String literal;
    private final String literalLowerCase;

    public LiteralCommandNode(final String literal, final Command<S> command, final Predicate<S> requirement, final CommandNode<S> redirect, final RedirectModifier<S> modifier, final boolean forks) {
        super(command, requirement, redirect, modifier, forks);
        this.literal = literal;
        this.literalLowerCase = literal.toLowerCase(Locale.ROOT);
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String getName() {
        return literal;
    }

    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int end = parse(reader);
        if (end > -1) {
            contextBuilder.withNode(this, StringRange.between(start, end));
            return;
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, literal);
    }

    private int parse(final StringReader reader) {
        final int start = reader.getCursor();
        if (reader.canRead(literal.length())) {
            final int end = start + literal.length();
            if (reader.getString().substring(start, end).equals(literal)) {
                reader.setCursor(end);
                if (!reader.canRead() || reader.peek() == ' ') {
                    return end;
                } else {
                    reader.setCursor(start);
                }
            }
        }
        return -1;
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (literalLowerCase.startsWith(builder.getRemainingLowerCase())) {
            return builder.suggest(literal).buildFuture();
        } else {
            return Suggestions.empty();
        }
    }

    @Override
    public boolean isValidInput(final String input) {
        return parse(new StringReader(input)) > -1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof LiteralCommandNode)) return false;

        final LiteralCommandNode that = (LiteralCommandNode) o;

        if (!literal.equals(that.literal)) return false;
        return super.equals(o);
    }

    @Override
    public String getUsageText() {
        return literal;
    }

    @Override
    public int hashCode() {
        int result = literal.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public LiteralArgumentBuilder<S> createBuilder() {
        final LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(this.literal);
        builder.requires(getRequirement());
        builder.forward(getRedirect(), getRedirectModifier(), isFork());
        if (getCommand() != null) {
            builder.executes(getCommand());
        }
        return builder;
    }

    @Override
    protected String getSortedKey() {
        return literal;
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.singleton(literal);
    }

    @Override
    public String toString() {
        return "<literal " + literal + ">";
    }
}
