package net.minecraft.commands.tree;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public class LiteralCommandNode extends CommandNode {
    private final String literal;

    public LiteralCommandNode(String literal, Command command) {
        super(command);
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String parse(String command, CommandContextBuilder contextBuilder) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        String expected = literal + (command.length() > literal.length() ? CommandDispatcher.ARGUMENT_SEPARATOR : "");

        if (!command.startsWith(expected)) {
            throw new IllegalArgumentSyntaxException();
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
        if (!getChildren().equals(that.getChildren())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = literal.hashCode();
        result = 31 * result + getChildren().hashCode();
        return result;
    }
}
