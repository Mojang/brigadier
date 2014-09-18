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
    public void parse(String command) throws IllegalCommandArgumentException {
        if (!command.equals(literal)) {
            throw new IllegalCommandArgumentException();
        }
    }
}
