package com.mojang.brigadier;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommandDispatcher<T> {
    private static final Predicate<CommandNode> HAS_COMMAND = new Predicate<CommandNode>() {
        @Override
        public boolean apply(@Nullable CommandNode input) {
            return input != null && (input.getCommand() != null || Iterables.any(input.getChildren(), HAS_COMMAND));
        }
    };

    public static final SimpleCommandExceptionType ERROR_UNKNOWN_COMMAND = new SimpleCommandExceptionType("unknown_command", "Unknown command");
    public static final String ARGUMENT_SEPARATOR = " ";

    private final RootCommandNode root = new RootCommandNode();

    public void register(LiteralArgumentBuilder command) {
        root.addChild(command.build());
    }

    public void execute(String command, T source) throws CommandException {
        CommandContext<T> context = parseNodes(root, command, new CommandContextBuilder<T>(source));
        context.getCommand().run(context);
    }

    protected CommandContext<T> parseNodes(CommandNode node, String command, CommandContextBuilder<T> contextBuilder) throws CommandException {
        CommandException exception = null;

        for (CommandNode child : node.getChildren()) {
            try {
                CommandContextBuilder<T> context = contextBuilder.copy();
                String remaining = child.parse(command, context);
                if (child.getCommand() != null) {
                    context.withCommand(child.getCommand());
                }
                return parseNodes(child, remaining, context);
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
        CommandContext<T> context = parseNodes(root, command, new CommandContextBuilder<T>(source));
        CommandNode base = Iterables.getLast(context.getNodes().keySet());
        List<CommandNode> children = Lists.newArrayList(Iterables.filter(base.getChildren(), HAS_COMMAND));
        boolean optional = base.getCommand() != null;

        if (children.isEmpty()) {
            return context.getInput();
        }

        Collections.sort(children, new Comparator<CommandNode>() {
            @Override
            public int compare(CommandNode o1, CommandNode o2) {
                return ComparisonChain.start()
                    .compareTrueFirst(o1 instanceof LiteralCommandNode, o2 instanceof LiteralCommandNode)
                    .result();
            }
        });

        StringBuilder result = new StringBuilder(context.getInput());
        result.append(ARGUMENT_SEPARATOR);
        if (optional) {
            result.append("[");
        } else if (children.size() > 1) {
            result.append("(");
        }

        for (int i = 0; i < children.size(); i++) {
            result.append(children.get(i).getUsageText());

            if (i < children.size() - 1) {
                result.append("|");
            }
        }

        if (optional) {
            result.append("]");
        } else if (children.size() > 1) {
            result.append(")");
        }

        return result.toString();
    }
}
