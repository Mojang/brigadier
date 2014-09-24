package net.minecraft.commands.tree;

import net.minecraft.commands.Command;
import net.minecraft.commands.arguments.CommandArgumentType;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.context.ParsedArgument;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

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
    public String parse(String command, CommandContextBuilder contextBuilder) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        ParsedArgument<T> parsed = type.parse(command);
        int start = parsed.getRaw().length();

        contextBuilder.withArgument(name, parsed);

        if (command.length() > start) {
            return command.substring(start + 1);
        } else {
            return "";
        }
    }
}
