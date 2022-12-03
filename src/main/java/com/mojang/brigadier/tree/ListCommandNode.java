package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ListCommandNode<S, T> extends ArgumentCommandNode<S, T> {
    public ListCommandNode(String name, ArgumentType<T> type, Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks, SuggestionProvider<S> customSuggestions) {
        super(name, type, command, requirement, redirect, modifier, forks, customSuggestions);
    }

    @Override
    public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final T result = getType().parse(reader);

        ParsedArgument<S, List<T>> parsed = (ParsedArgument<S, List<T>>) contextBuilder.getArguments().get(getName());
        if (parsed == null) {
            parsed = new ParsedArgument<>(start, reader.getCursor(), new ArrayList<>());
        } else {
            parsed = new ParsedArgument<>(parsed.getRange().getStart(), reader.getCursor(), parsed.getResult());
        }
        parsed.getResult().add(result);

        contextBuilder.withArgument(getName(), parsed);
        contextBuilder.withNode(this, parsed.getRange());
    }
}
