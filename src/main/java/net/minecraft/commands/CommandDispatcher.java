package net.minecraft.commands;

import com.google.common.collect.Maps;
import net.minecraft.commands.builder.CommandBuilder;
import net.minecraft.commands.exceptions.CommandException;
import net.minecraft.commands.exceptions.UnknownCommandException;

import java.util.Map;

public class CommandDispatcher {
    private final Map<String, Runnable> commands = Maps.newHashMap();

    public void register(CommandBuilder command) {
        if (commands.containsKey(command.getName())) {
            throw new IllegalArgumentException("New command " + command.getName() + " conflicts with existing command " + command.getName());
        }
        commands.put(command.getName(), command.getExecutor());
    }

    public void execute(String command) throws CommandException {
        Runnable runnable = commands.get(command);
        if (runnable == null) {
            throw new UnknownCommandException();
        }

        runnable.run();
    }
}
