package net.minecraft.commands;

import net.minecraft.commands.builder.LiteralArgumentBuilder;
import net.minecraft.commands.context.CommandContext;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.CommandException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;
import net.minecraft.commands.exceptions.UnknownCommandException;
import net.minecraft.commands.tree.CommandNode;
import net.minecraft.commands.tree.RootCommandNode;

public class CommandDispatcher {
    public static final String ARGUMENT_SEPARATOR = " ";

    private final RootCommandNode root = new RootCommandNode();

    public void register(LiteralArgumentBuilder command) {
        root.addChild(command.build());
    }

    public void execute(String command) throws CommandException {
        CommandContext context = parseNodes(root, command, new CommandContextBuilder());
        context.getCommand().run(context);
    }

    protected CommandContext parseNodes(CommandNode node, String command, CommandContextBuilder contextBuilder) throws IllegalArgumentSyntaxException, ArgumentValidationException, UnknownCommandException {
        IllegalArgumentSyntaxException exception = null;

        for (CommandNode child : node.getChildren()) {
            try {
                CommandContextBuilder context = contextBuilder.copy();
                String remaining = child.parse(command, context);
                if (child.getCommand() != null) {
                    context.withCommand(child.getCommand());
                }
                return parseNodes(child, remaining, context);
            } catch (IllegalArgumentSyntaxException ex) {
                exception = ex;
            }
        }

        if (exception != null) {
            throw exception;
        }
        if (command.length() > 0) {
            throw new UnknownCommandException();
        }

        return contextBuilder.build();
    }
}
