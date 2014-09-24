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
        if (!command.startsWith(literal)) {
            throw new IllegalArgumentSyntaxException();
        }

        int start = literal.length();
        if (command.length() > start) {
            return command.substring(start + 1);
        } else {
            return "";
        }
    }
}
