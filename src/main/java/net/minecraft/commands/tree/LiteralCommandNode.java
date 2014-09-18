package net.minecraft.commands.tree;

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
}
