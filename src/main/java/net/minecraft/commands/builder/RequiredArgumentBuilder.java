package net.minecraft.commands.builder;

import net.minecraft.commands.arguments.CommandArgumentType;
import net.minecraft.commands.tree.ArgumentCommandNode;

public class RequiredArgumentBuilder<T> extends ArgumentBuilder<RequiredArgumentBuilder<T>> {
    private final String name;
    private final CommandArgumentType<T> type;

    protected RequiredArgumentBuilder(String name, CommandArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <T> RequiredArgumentBuilder<T> argument(String name, CommandArgumentType<T> type) {
        return new RequiredArgumentBuilder<T>(name, type);
    }

    @Override
    protected RequiredArgumentBuilder<T> getThis() {
        return this;
    }

    public CommandArgumentType<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArgumentCommandNode<T> build() {
        ArgumentCommandNode<T> result = new ArgumentCommandNode<T>(getName(), getType(), getExecutor());

        for (ArgumentBuilder argument : getArguments()) {
            result.addChild(argument.build());
        }

        return result;
    }
}
