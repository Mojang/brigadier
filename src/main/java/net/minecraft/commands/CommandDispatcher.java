package net.minecraft.commands;

import com.google.common.collect.Maps;
import net.minecraft.commands.builder.LiteralArgumentBuilder;
import net.minecraft.commands.exceptions.CommandException;
import net.minecraft.commands.exceptions.UnknownCommandException;
import net.minecraft.commands.tree.LiteralCommandNode;

import java.util.Map;

public class CommandDispatcher {
    private final Map<String, LiteralCommandNode> commands = Maps.newHashMap();

    public void register(LiteralArgumentBuilder command) {
        if (commands.containsKey(command.getLiteral())) {
            throw new IllegalArgumentException("New command " + command.getLiteral() + " conflicts with existing command " + command.getLiteral());
        }
        commands.put(command.getLiteral(), command.build());
    }

    public void execute(String command) throws CommandException {
        LiteralCommandNode node = commands.get(command);
        if (node == null) {
            throw new UnknownCommandException();
        }

        node.getExecutor().run();
    }
}
