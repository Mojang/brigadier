package net.minecraft.commands.tree;

import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public class LiteralCommandNode extends CommandNode {
    private final String literal;
    private final Runnable executor;

    public LiteralCommandNode(String literal, Runnable executor) {
        this.literal = literal;
        this.executor = executor;
    }

    public String getLiteral() {
        return literal;
    }

    public Runnable getExecutor() {
        return executor;
    }

    @Override
    public CommandNode parse(String command) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        if (command.startsWith(literal)) {
            int start = literal.length() + 1;

            if (start < command.length()) {
                String result = command.substring(start);

                for (CommandNode node : getChildren()) {
                    try {
                        return node.parse(result);
                    } catch (IllegalArgumentSyntaxException ignored) {
                    }
                }

                throw new IllegalArgumentSyntaxException();
            } else {
                return this;
            }
        } else {
            throw new IllegalArgumentSyntaxException();
        }
    }
}
