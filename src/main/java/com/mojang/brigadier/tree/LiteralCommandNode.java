package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

import java.util.Set;
import java.util.function.Predicate;

public class LiteralCommandNode<S> extends CommandNode<S> {
    public static final ParameterizedCommandExceptionType ERROR_INCORRECT_LITERAL = new ParameterizedCommandExceptionType("argument.literal.incorrect", "Expected literal ${expected}", "expected");

    private final String literal;

    public LiteralCommandNode(String literal, Command<S> command, Predicate<S> requirement) {
        super(command, requirement);
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    protected Object getMergeKey() {
        return literal;
    }

    @Override
    public String parse(String command, CommandContextBuilder<S> contextBuilder) throws CommandException {
        String expected = literal + (command.length() > literal.length() ? CommandDispatcher.ARGUMENT_SEPARATOR : "");

        if (!command.startsWith(expected)) {
            throw ERROR_INCORRECT_LITERAL.create(literal);
        }

        contextBuilder.withNode(this, literal);
        int start = literal.length();
        return command.substring(start);
    }

    @Override
    public void listSuggestions(String command, Set<String> output, CommandContextBuilder<S> contextBuilder) {
        if (literal.startsWith(command)) {
            output.add(literal);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LiteralCommandNode)) return false;

        LiteralCommandNode that = (LiteralCommandNode) o;

        if (!literal.equals(that.literal)) return false;
        return super.equals(o);
    }

    @Override
    public String getUsageText() {
        return literal;
    }

    @Override
    public int hashCode() {
        int result = literal.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public LiteralArgumentBuilder<S> createBuilder() {
        final LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(this.literal);
        builder.requires(getRequirement());
        if (getCommand() != null) {
            builder.executes(getCommand());
        }
        return builder;
    }
}
