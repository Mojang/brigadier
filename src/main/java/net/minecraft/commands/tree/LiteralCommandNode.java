package net.minecraft.commands.tree;

import net.minecraft.commands.Command;
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
        String expected = literal + (command.length() > literal.length() ? " " : "");

        if (!command.startsWith(expected)) {
            throw new IllegalArgumentSyntaxException();
        }

        int start = expected.length();
        return command.substring(start);
    }
}
