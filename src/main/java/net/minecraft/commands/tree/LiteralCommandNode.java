package net.minecraft.commands.tree;

public class LiteralCommandNode extends CommandNode {
    private final String literal;

    public LiteralCommandNode(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
