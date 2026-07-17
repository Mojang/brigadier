package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ListElementArgumentType implements ArgumentType<String> {
    private final List<String> list;
    private Collection<String> examples;

    private ListElementArgumentType(List<String> list) {
        this.list = list;
    }

    private ListElementArgumentType(List<String> list, Collection<String> examples) {
        this.list = list;
        this.examples = examples;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readString();
        if (list.contains(input)) {
            return list.get(list.indexOf(input));
        } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerNoSuchElement().create(input);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        list.stream()
            .filter(it -> it.startsWith(builder.getRemaining().toLowerCase()))
            .distinct()
            .sorted()
            .collect(Collectors.toList())
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return examples;
    }

    public static <T> ListElementArgumentType list(final List<T> list) {
        return new ListElementArgumentType(
            list.stream().map(Object::toString).collect(Collectors.toList())
        );
    }

    public static <T> ListElementArgumentType list(
        final List<T> list, final Collection<String> examples
    ) {
        return new ListElementArgumentType(
            list.stream().map(Object::toString).collect(Collectors.toList()), examples
        );
    }

    public static String getValue(
        final CommandContext<?> context, final String name
    ) {
        return context.getArgument(name, String.class);
    }
}
