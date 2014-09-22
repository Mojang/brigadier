package net.minecraft.commands.tree;

import net.minecraft.commands.exceptions.IllegalCommandArgumentException;

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
    public CommandNode parse(String command) throws IllegalCommandArgumentException {
        if (command.startsWith(literal)) {
            int start = literal.length() + 1;

            if (start < command.length()) {
                String result = command.substring(start);

                for (CommandNode node : getChildren()) {
                    try {
                        return node.parse(result);
                    } catch (IllegalCommandArgumentException ignored) {
                    }
                }

                throw new IllegalCommandArgumentException();
            } else {
                return this;
            }
        } else {
            throw new IllegalCommandArgumentException();
        }
    }
}
