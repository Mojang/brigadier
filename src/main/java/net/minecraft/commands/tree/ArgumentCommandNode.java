package net.minecraft.commands.tree;

import net.minecraft.commands.arguments.CommandArgumentType;

public class ArgumentCommandNode extends CommandNode {
    private final String name;
    private final CommandArgumentType type;

    public ArgumentCommandNode(String name, CommandArgumentType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public CommandArgumentType getType() {
        return type;
    }
}
