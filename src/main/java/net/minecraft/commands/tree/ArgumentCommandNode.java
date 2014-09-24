package net.minecraft.commands.tree;

import net.minecraft.commands.arguments.CommandArgumentType;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public class ArgumentCommandNode<T> extends CommandNode {
    private final String name;
    private final CommandArgumentType<T> type;

    public ArgumentCommandNode(String name, CommandArgumentType<T> type, Runnable executor) {
        super(executor);
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
    public CommandNode parse(String command) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        CommandArgumentType.CommandArgumentParseResult<T> parsed = type.parse(command);
        int start = parsed.getRaw().length() + 1;

        if (start < command.length()) {
            String result = command.substring(start);

            for (CommandNode node : getChildren()) {
                try {
                    return node.parse(result);
                } catch (IllegalArgumentSyntaxException ignored) {
                }
            }

            throw new IllegalArgumentSyntaxException();
        } else {
            return this;
        }
    }
}
