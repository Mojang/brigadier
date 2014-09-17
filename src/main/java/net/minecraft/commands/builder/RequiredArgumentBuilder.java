package net.minecraft.commands.builder;

import net.minecraft.commands.arguments.CommandArgumentType;
import net.minecraft.commands.tree.ArgumentCommandNode;

public class RequiredArgumentBuilder extends ArgumentBuilder {
    private final String name;
    private final CommandArgumentType type;

    protected RequiredArgumentBuilder(String name, CommandArgumentType type) {
        this.name = name;
        this.type = type;
    }

    public static RequiredArgumentBuilder argument(String name, CommandArgumentType type) {
        return new RequiredArgumentBuilder(name, type);
    }

    public CommandArgumentType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArgumentCommandNode build() {
        ArgumentCommandNode result = new ArgumentCommandNode(getName(), getType());

        for (ArgumentBuilder argument : getArguments()) {
            result.addChild(argument.build());
        }

        return result;
    }
}
