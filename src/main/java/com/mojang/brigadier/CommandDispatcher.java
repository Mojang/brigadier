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

public class CommandDispatcher<T> {
    private static final Predicate<CommandNode> HAS_COMMAND = new Predicate<CommandNode>() {
        @Override
        public boolean test(CommandNode input) {
            return input != null && (input.getCommand() != null || input.getChildren().stream().anyMatch(HAS_COMMAND));
        }
    };

    public static final SimpleCommandExceptionType ERROR_UNKNOWN_COMMAND = new SimpleCommandExceptionType("command.unknown", "Unknown command");
    public static final String ARGUMENT_SEPARATOR = " ";
    private static final String USAGE_OPTIONAL_OPEN = "[";
    private static final String USAGE_OPTIONAL_CLOSE = "]";
    private static final String USAGE_REQUIRED_OPEN = "(";
    private static final String USAGE_REQUIRED_CLOSE = ")";
    private static final String USAGE_OR = "|";

    private final RootCommandNode root = new RootCommandNode();

    public void register(LiteralArgumentBuilder command) {
        root.addChild(command.build());
    }

    public void execute(String command, T source) throws CommandException {
        CommandContext<T> context = parseNodes(root, command, new CommandContextBuilder<>(source));
        context.getCommand().run(context);
    }

    private CommandContext<T> parseNodes(CommandNode node, String command, CommandContextBuilder<T> contextBuilder) throws CommandException {
        CommandException exception = null;

        for (CommandNode child : node.getChildren()) {
            try {
                CommandContextBuilder<T> context = contextBuilder.copy();
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

    public String getUsage(String command, T source) throws CommandException {
        CommandContext<T> context = parseNodes(root, command, new CommandContextBuilder<>(source));
        CommandNode base = Iterables.getLast(context.getNodes().keySet());
        List<CommandNode> children = base.getChildren().stream().filter(HAS_COMMAND).collect(Collectors.toList());
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

    private Set<String> findSuggestions(CommandNode node, String command, CommandContextBuilder<T> contextBuilder, Set<String> result) {
        for (CommandNode child : node.getChildren()) {
            try {
                CommandContextBuilder<T> context = contextBuilder.copy();
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

    public String[] getCompletionSuggestions(String command, T source) {
        final Set<String> nodes = findSuggestions(root, command, new CommandContextBuilder<>(source), Sets.newLinkedHashSet());

        return nodes.toArray(new String[nodes.size()]);
    }
}
