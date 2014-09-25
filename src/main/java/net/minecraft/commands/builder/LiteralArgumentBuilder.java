package net.minecraft.commands.builder;

import net.minecraft.commands.tree.CommandNode;
import net.minecraft.commands.tree.LiteralCommandNode;

public class LiteralArgumentBuilder extends ArgumentBuilder<LiteralArgumentBuilder> {
    private final String literal;

    protected LiteralArgumentBuilder(String literal) {
        this.literal = literal;
    }

    public static LiteralArgumentBuilder literal(String name) {
        return new LiteralArgumentBuilder(name);
    }

    @Override
    protected LiteralArgumentBuilder getThis() {
        return this;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public LiteralCommandNode build() {
        LiteralCommandNode result = new LiteralCommandNode(getLiteral(), getCommand());

        for (CommandNode argument : getArguments()) {
            result.addChild(argument);
        }

        return result;
    }
}
