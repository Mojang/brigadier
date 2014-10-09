package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.CommandArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandException;

public class ArgumentCommandNode<T> extends CommandNode {
    private final String name;
    private final CommandArgumentType<T> type;

    public ArgumentCommandNode(String name, CommandArgumentType<T> type, Command command) {
        super(command);
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
    protected Object getMergeKey() {
        return name;
    }

    @Override
    public String getUsageText() {
        return "<" + name + ">";
    }

    @Override
    public String parse(String command, CommandContextBuilder<?> contextBuilder) throws CommandException {
        ParsedArgument<T> parsed = type.parse(command);
        int start = parsed.getRaw().length();

        contextBuilder.withArgument(name, parsed);
        contextBuilder.withNode(this, parsed.getRaw());

        if (command.length() > start) {
            return command.substring(start + 1);
        } else {
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArgumentCommandNode)) return false;

        ArgumentCommandNode that = (ArgumentCommandNode) o;

        if (!name.equals(that.name)) return false;
        if (!type.equals(that.type)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * super.hashCode();
        return result;
    }
}
