package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandSuggestions;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ArgumentCommandNode<S, T> extends CommandNode<S> {
    private static final String USAGE_ARGUMENT_OPEN = "<";
    private static final String USAGE_ARGUMENT_CLOSE = ">";

    private final String name;
    private final ArgumentType<T> type;
    private final CommandSuggestions.Provider<S> customSuggestions;

    public ArgumentCommandNode(final String name, final ArgumentType<T> type, final Command<S> command, final Predicate<S> requirement, final CommandNode<S> redirect, final RedirectModifier<S> modifier, final CommandSuggestions.Provider<S> customSuggestions) {
        super(command, requirement, redirect, modifier);
        this.name = name;
        this.type = type;
        this.customSuggestions = customSuggestions;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsageText() {
        return USAGE_ARGUMENT_OPEN + name + USAGE_ARGUMENT_CLOSE;
    }

    public CommandSuggestions.Provider<S> getCustomSuggestions() {
        return customSuggestions;
    }

    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final T result = type.parse(reader, contextBuilder);
        final ParsedArgument<S, T> parsed = new ParsedArgument<>(start, reader.getCursor(), result);

        contextBuilder.withArgument(name, parsed);
        contextBuilder.withNode(this, parsed.getRange());
    }

    @Override
    public CompletableFuture<Collection<String>> listSuggestions(final CommandContext<S> context, final String command) throws CommandSyntaxException {
        if (customSuggestions == null) {
            return type.listSuggestions(context, command);
        } else {
            return customSuggestions.getSuggestions(context, command);
        }
    }

    @Override
    public RequiredArgumentBuilder<S, T> createBuilder() {
        final RequiredArgumentBuilder<S, T> builder = RequiredArgumentBuilder.argument(name, type);
        builder.requires(getRequirement());
        builder.redirect(getRedirect(), getRedirectModifier());
        builder.suggests(customSuggestions);
        if (getCommand() != null) {
            builder.executes(getCommand());
        }
        return builder;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ArgumentCommandNode)) return false;

        final ArgumentCommandNode that = (ArgumentCommandNode) o;

        if (!name.equals(that.name)) return false;
        if (!type.equals(that.type)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    protected String getSortedKey() {
        return name;
    }
}
