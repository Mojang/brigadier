package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

public class LiteralCommandNode extends CommandNode {
    public static final ParameterizedCommandExceptionType ERROR_INCORRECT_LITERAL = new ParameterizedCommandExceptionType("incorrect_literal", "Expected literal ${expected}", "expected");

    private final String literal;

    public LiteralCommandNode(String literal, Command command) {
        super(command);
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
    public String parse(String command, CommandContextBuilder<?> contextBuilder) throws CommandException {
        String expected = literal + (command.length() > literal.length() ? CommandDispatcher.ARGUMENT_SEPARATOR : "");

        if (!command.startsWith(expected)) {
            throw ERROR_INCORRECT_LITERAL.create(expected);
        }

        int start = expected.length();
        return command.substring(start);
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
    public int hashCode() {
        int result = literal.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }
}
