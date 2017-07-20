package com.mojang.brigadier.arguments;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.FixedParsedArgument;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Arrays;
import java.util.Set;

public class CommandArgumentType<T> implements ArgumentType<ParseResults<T>> {
    public static <S> CommandArgumentType<S> command() {
        return new CommandArgumentType<S>();
    }

    @Override
    public <S> ParsedArgument<S, ParseResults<T>> parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        final ParseResults<S> parse = contextBuilder.getDispatcher().parse(command, contextBuilder.getSource());

        //noinspection unchecked
        return new FixedParsedArgument<>(command, (ParseResults<T>) parse);
    }

    @Override
    public <S> void listSuggestions(String command, Set<String> output, CommandContextBuilder<S> contextBuilder) {
        final String[] suggestions = contextBuilder.getDispatcher().getCompletionSuggestions(command, contextBuilder.getSource());
        output.addAll(Arrays.asList(suggestions));
    }
}
