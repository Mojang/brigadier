package net.minecraft.commands;

import net.minecraft.commands.builder.LiteralArgumentBuilder;
import net.minecraft.commands.context.CommandContext;
import net.minecraft.commands.context.CommandContextBuilder;
import net.minecraft.commands.exceptions.CommandException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;
import net.minecraft.commands.exceptions.UnknownCommandException;
import net.minecraft.commands.tree.CommandNode;
import net.minecraft.commands.tree.RootCommandNode;

public class CommandDispatcher {
    private final RootCommandNode root = new RootCommandNode();

    public void register(LiteralArgumentBuilder command) {
        root.addChild(command.build());
    }

    public void execute(String command) throws CommandException {
        CommandContextBuilder contextBuilder = new CommandContextBuilder();
        CommandNode node = root;

        while (command.length() > 0 && !node.getChildren().isEmpty()) {
            IllegalArgumentSyntaxException exception = null;

            for (CommandNode child : node.getChildren()) {
                try {
                    command = child.parse(command, contextBuilder);
                    if (child.getCommand() != null) {
                        contextBuilder.withCommand(child.getCommand());
                    }
                    node = child;
                    break;
                } catch (IllegalArgumentSyntaxException ex) {
                    exception = ex;
                }
            }

            if (exception != null) {
                break;
            }
        }

        if (command.length() > 0) {
            throw new UnknownCommandException();
        }

        CommandContext context = contextBuilder.build();
        context.getCommand().run(context);
    }
}
