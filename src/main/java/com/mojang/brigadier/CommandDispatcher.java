package com.mojang.brigadier;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandDispatcher<S> {
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_COMMAND = new SimpleCommandExceptionType("command.unknown", "Unknown command");
    public static final String ARGUMENT_SEPARATOR = " ";
    private static final String USAGE_OPTIONAL_OPEN = "[";
    private static final String USAGE_OPTIONAL_CLOSE = "]";
    private static final String USAGE_REQUIRED_OPEN = "(";
    private static final String USAGE_REQUIRED_CLOSE = ")";
    private static final String USAGE_OR = "|";

    private final RootCommandNode<S> root = new RootCommandNode<>();
    private final Predicate<CommandNode<S>> hasCommand = new Predicate<CommandNode<S>>() {
        @Override
        public boolean test(CommandNode<S> input) {
            return input != null && (input.getCommand() != null || input.getChildren().stream().anyMatch(hasCommand));
        }
    };

    public void register(LiteralArgumentBuilder<S> command) {
        root.addChild(command.build());
    }

    public void execute(String command, S source) throws CommandException {
        CommandContext<S> context = parseNodes(root, command, new CommandContextBuilder<>(source));
        context.getCommand().run(context);
    }

    private CommandContext<S> parseNodes(CommandNode<S> node, String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        CommandException exception = null;

        for (CommandNode<S> child : node.getChildren()) {
            try {
                CommandContextBuilder<S> context = contextBuilder.copy();
                String remaining = child.parse(command, context);
                if (child.getCommand() != null) {
                    context.withCommand(child.getCommand());
                }
                if (remaining.isEmpty()) {
                    return context.build();
                } else {
                    return parseNodes(child, remaining.substring(1), context);
                }
            } catch (CommandException ex) {
                exception = ex;
            }
        }

        if (command.length() > 0) {
            if (exception != null) {
                throw exception;
            }
            throw ERROR_UNKNOWN_COMMAND.create();
        }

        return contextBuilder.build();
    }

    public String getUsage(String command, S source) throws CommandException {
        CommandContext<S> context = parseNodes(root, command, new CommandContextBuilder<>(source));
        CommandNode<S> base = Iterables.getLast(context.getNodes().keySet());
        List<CommandNode<S>> children = base.getChildren().stream().filter(hasCommand).collect(Collectors.toList());
        boolean optional = base.getCommand() != null;

        if (children.isEmpty()) {
            return context.getInput();
        }

        children.sort((o1, o2) -> ComparisonChain.start()
            .compareTrueFirst(o1 instanceof LiteralCommandNode, o2 instanceof LiteralCommandNode)
            .result());

        StringBuilder result = new StringBuilder(context.getInput());
        result.append(ARGUMENT_SEPARATOR);
        if (optional) {
            result.append(USAGE_OPTIONAL_OPEN);
        } else if (children.size() > 1) {
            result.append(USAGE_REQUIRED_OPEN);
        }

        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i).getUsageText());

            if (i < children.size() - 1) {
                result.append(USAGE_OR);
            }
        }

        if (optional) {
            result.append(USAGE_OPTIONAL_CLOSE);
        } else if (children.size() > 1) {
            result.append(USAGE_REQUIRED_CLOSE);
        }

        return result.toString();
    }

    private Set<String> findSuggestions(CommandNode<S> node, String command, CommandContextBuilder<S> contextBuilder, Set<String> result) {
        for (CommandNode<S> child : node.getChildren()) {
            try {
                CommandContextBuilder<S> context = contextBuilder.copy();
                String remaining = child.parse(command, context);
                if (remaining.isEmpty()) {
                    child.listSuggestions(command, result);
                } else {
                    return findSuggestions(child, remaining.substring(1), context, result);
                }
            } catch (CommandException e) {
                child.listSuggestions(command, result);
            }
        }

        return result;
    }

    public String[] getCompletionSuggestions(String command, S source) {
        final Set<String> nodes = findSuggestions(root, command, new CommandContextBuilder<>(source), Sets.newLinkedHashSet());

        return nodes.toArray(new String[nodes.size()]);
    }
}
