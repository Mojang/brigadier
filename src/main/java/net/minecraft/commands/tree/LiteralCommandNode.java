package net.minecraft.commands.tree;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.CommandException;
import net.minecraft.commands.exceptions.ParameterizedCommandExceptionType;

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
