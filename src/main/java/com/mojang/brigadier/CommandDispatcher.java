package com.mojang.brigadier;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        final LiteralCommandNode<S> build = command.build();
        root.addChild(build);
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

    public String[] getAllUsage(CommandNode<S> node, S source) {
        final ArrayList<String> result = Lists.newArrayList();
        getAllUsage(node, source, result,  "");
        return result.toArray(new String[result.size()]);
    }

    private void getAllUsage(CommandNode<S> node, S source, ArrayList<String> result, String prefix) {
        if (!node.canUse(source)) {
            return;
        }

        if (node.getCommand() != null) {
            result.add(prefix);
        }

        if (!node.getChildren().isEmpty()) {
            for (final CommandNode<S> child : node.getChildren()) {
                getAllUsage(child, source, result, prefix.isEmpty() ? child.getUsageText() : prefix + ARGUMENT_SEPARATOR + child.getUsageText());
            }
        }
    }

    public Map<CommandNode<S>, String> getSmartUsage(CommandNode<S> node, S source) {
        Map<CommandNode<S>, String> result = Maps.newLinkedHashMap();

        final boolean optional = node.getCommand() != null;
        for (CommandNode<S> child : node.getChildren()) {
            String usage = getSmartUsage(child, source, optional, false);
            if (usage != null) {
                result.put(child, usage);
            }
        }
        return result;
    }

    private String getSmartUsage(CommandNode<S> node, S source, boolean optional, boolean deep) {
        if (!node.canUse(source)) {
            return null;
        }

        String self = optional ? USAGE_OPTIONAL_OPEN + node.getUsageText() + USAGE_OPTIONAL_CLOSE : node.getUsageText();
        boolean childOptional = node.getCommand() != null;
        String open = childOptional ? USAGE_OPTIONAL_OPEN : USAGE_REQUIRED_OPEN;
        String close = childOptional ? USAGE_OPTIONAL_CLOSE : USAGE_REQUIRED_CLOSE;

        if (!deep) {
            final Collection<CommandNode<S>> children = node.getChildren().stream().filter(c -> c.canUse(source)).collect(Collectors.toList());
            if (children.size() == 1) {
                final String usage = getSmartUsage(children.iterator().next(), source, childOptional, childOptional);
                if (usage != null) {
                    return self + ARGUMENT_SEPARATOR + usage;
                }
            } else if (children.size() > 1) {
                Set<String> childUsage = Sets.newLinkedHashSet();
                for (final CommandNode<S> child : children) {
                    final String usage = getSmartUsage(child, source, childOptional, true);
                    if (usage != null) {
                        childUsage.add(usage);
                    }
                }
                if (childUsage.size() == 1) {
                    final String usage = childUsage.iterator().next();
                    return self + ARGUMENT_SEPARATOR + (childOptional ? USAGE_OPTIONAL_OPEN + usage + USAGE_OPTIONAL_CLOSE : usage);
                } else if (childUsage.size() > 1) {
                    StringBuilder builder = new StringBuilder(open);
                    int count = 0;
                    for (final CommandNode<S> child : children) {
                        if (count > 0) {
                            builder.append(USAGE_OR);
                        }
                        builder.append(child.getUsageText());
                        count++;
                    }
                    if (count > 0) {
                        builder.append(close);
                        return self + ARGUMENT_SEPARATOR + builder.toString();
                    }
                }
            }
        }

        return self;
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
