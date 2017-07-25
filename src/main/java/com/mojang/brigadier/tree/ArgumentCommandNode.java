package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
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
        String usage = name;
        if (type.getUsageText() != null) {
            usage += ": " + type.getUsageText();
        }
        usage = USAGE_ARGUMENT_OPEN + usage + USAGE_ARGUMENT_CLOSE;
        if (type.getUsageSuffix() != null) {
            usage += type.getUsageSuffix();
        }
        return usage;
    }

    @Override
    public void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandException {
        int start = reader.getCursor();
        T result = type.parse(reader, contextBuilder);
        ParsedArgument<S, T> parsed = new ParsedArgument<>(reader.getString().substring(start, reader.getCursor()), result);

        contextBuilder.withArgument(name, parsed);
        contextBuilder.withNode(this, parsed.getRaw());
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

    @Override
    protected String getSortedKey() {
        return name;
    }
}
