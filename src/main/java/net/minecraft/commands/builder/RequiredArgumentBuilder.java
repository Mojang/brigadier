package net.minecraft.commands.builder;

import net.minecraft.commands.arguments.CommandArgumentType;

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
}
