package net.minecraft.commands;

import com.google.common.collect.Maps;
import net.minecraft.commands.builder.CommandBuilder;
import net.minecraft.commands.exceptions.CommandException;
import net.minecraft.commands.exceptions.UnknownCommandException;
import net.minecraft.commands.tree.LiteralCommandNode;

import java.util.Map;

public class CommandDispatcher {
    private final Map<String, LiteralCommandNode> commands = Maps.newHashMap();

    public void register(CommandBuilder command) {
        if (commands.containsKey(command.getName())) {
            throw new IllegalArgumentException("New command " + command.getName() + " conflicts with existing command " + command.getName());
        }
        commands.put(command.getName(), command.build());
    }

    public void execute(String command) throws CommandException {
        LiteralCommandNode node = commands.get(command);
        if (node == null) {
            throw new UnknownCommandException();
        }

        node.getExecutor().run();
    }
}
