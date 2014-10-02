package net.minecraft.commands.tree;

import net.minecraft.commands.Command;
import net.minecraft.commands.arguments.CommandArgumentType;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.context.ParsedArgument;
import net.minecraft.commands.exceptions.CommandException;

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
    public String parse(String command, CommandContextBuilder<?> contextBuilder) throws CommandException {
        ParsedArgument<T> parsed = type.parse(command);
        int start = parsed.getRaw().length();

        contextBuilder.withArgument(name, parsed);

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
