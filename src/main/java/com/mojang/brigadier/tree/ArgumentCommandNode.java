package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;

import java.util.Set;
import java.util.function.Predicate;

public class ArgumentCommandNode<S, T> extends CommandNode<S> {
    private static final String USAGE_ARGUMENT_OPEN = "<";
    private static final String USAGE_ARGUMENT_CLOSE = ">";

    private final String name;
    private final ArgumentType<T> type;

    public ArgumentCommandNode(String name, ArgumentType<T> type, Command<S> command, Predicate<S> requirement) {
        super(command, requirement);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ArgumentType<T> getType() {
        return type;
    }

    @Override
    protected Object getMergeKey() {
        return name;
    }

    @Override
    public String getUsageText() {
        return USAGE_ARGUMENT_OPEN + name + USAGE_ARGUMENT_CLOSE;
    }

    @Override
    public String parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        ParsedArgument<S, T> parsed = type.parse(command, contextBuilder);
        int start = parsed.getRaw().length();

        contextBuilder.withArgument(name, parsed);
        contextBuilder.withNode(this, parsed.getRaw());

        if (command.length() > start) {
            return command.substring(start);
        } else {
            return "";
        }
    }

    @Override
    public void listSuggestions(String command, Set<String> output, CommandContextBuilder<S> contextBuilder) {
        type.listSuggestions(command, output, contextBuilder);
    }

    @Override
    public RequiredArgumentBuilder<S, T> createBuilder() {
        final RequiredArgumentBuilder<S, T> builder = RequiredArgumentBuilder.argument(name, type);
        builder.requires(getRequirement());
        if (getCommand() != null) {
            builder.executes(getCommand());
        }
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArgumentCommandNode)) return false;

        ArgumentCommandNode that = (ArgumentCommandNode) o;

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
}
