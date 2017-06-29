package com.mojang.brigadier;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandDispatcher<S> {
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_COMMAND = new SimpleCommandExceptionType("command.unknown.command", "Unknown command");
    public static final ParameterizedCommandExceptionType ERROR_UNKNOWN_ARGUMENT = new ParameterizedCommandExceptionType("command.unknown.argument", "Incorrect argument for command, couldn't parse: ${argument}", "argument");

    public static final String ARGUMENT_SEPARATOR = " ";
    public static final char ARGUMENT_SEPARATOR_CHAR = ' ';
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

    public int execute(String input, S source) throws CommandException {
        final ParseResults<S> parse = parse(input, source);
        if (parse.getRemaining().length() > 0) {
            if (parse.getExceptions().size() == 1) {
                throw parse.getExceptions().values().iterator().next();
            } else if (parse.getContext().getInput().isEmpty()) {
                throw ERROR_UNKNOWN_COMMAND.create();
            } else {
                throw ERROR_UNKNOWN_ARGUMENT.create(parse.getRemaining());
            }
        }
        CommandContext<S> context = parse.getContext().build();
        Command<S> command = context.getCommand();
        if (command == null) {
            throw ERROR_UNKNOWN_COMMAND.create();
        }
        return command.run(context);
    }

    public ParseResults<S> parse(String command, S source) throws CommandException {
        return parseNodes(root, command, new CommandContextBuilder<>(source));
    }

    private ParseResults<S> parseNodes(CommandNode<S> node, String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        final S source = contextBuilder.getSource();
        Map<CommandNode<S>, CommandException> errors = Maps.newHashMap();

        for (CommandNode<S> child : node.getChildren()) {
            if (!child.canUse(source)) {
                continue;
            }
            CommandContextBuilder<S> context = contextBuilder.copy();
            String remaining;
            try {
                remaining = child.parse(command, context);
            } catch (CommandException ex) {
                errors.put(child, ex);
                continue;
            }

            if (child.getCommand() != null) {
                context.withCommand(child.getCommand());
            }
            if (remaining.isEmpty()) {
                return new ParseResults<>(context);
            } else {
                return parseNodes(child, remaining.substring(1), context);
            }
        }

        return new ParseResults<>(contextBuilder, command, errors);
    }

    public String getUsage(String command, S source) throws CommandException {
        final ParseResults<S> parse = parseNodes(root, command, new CommandContextBuilder<>(source));
        if (parse.getContext().getNodes().isEmpty()) {
            throw ERROR_UNKNOWN_COMMAND.create();
        }

        CommandContext<S> context = parse.getContext().build();
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
        final S source = contextBuilder.getSource();
        for (CommandNode<S> child : node.getChildren()) {
            if (!child.canUse(source)) {
                continue;
            }
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

    public RootCommandNode<S> getRoot() {
        return root;
    }
}
