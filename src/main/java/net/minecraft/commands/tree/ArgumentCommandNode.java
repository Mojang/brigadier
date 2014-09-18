package net.minecraft.commands.tree;

import net.minecraft.commands.arguments.CommandArgumentType;
import net.minecraft.commands.exceptions.IllegalCommandArgumentException;

public class ArgumentCommandNode<T> extends CommandNode {
    private final String name;
    private final CommandArgumentType<T> type;

    public ArgumentCommandNode(String name, CommandArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public CommandArgumentType<T> getType() {
        return type;
    }

    @Override
    public void parse(String command) throws IllegalCommandArgumentException {
        type.parse(command);
    }
}
