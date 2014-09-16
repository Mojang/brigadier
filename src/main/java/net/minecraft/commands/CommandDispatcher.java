package net.minecraft.commands;

import com.google.common.collect.Maps;
import net.minecraft.commands.builder.CommandBuilder;
import net.minecraft.commands.exceptions.CommandException;
import net.minecraft.commands.exceptions.UnknownCommandException;

import java.util.Map;

public class CommandDispatcher {
    private final Map<String, Runnable> commands = Maps.newHashMap();

    public CommandBuilder createCommand(final String name) {
        return new CommandBuilder() {
            @Override
            public void onFinish() {
                if (commands.containsKey(name)) {
                    throw new IllegalArgumentException("New command " + name + " conflicts with existing command " + name);
                }
                commands.put(name, getCommandExecutor());
            }
        };
    }

    public void execute(String command) throws CommandException {
        Runnable runnable = commands.get(command);
        if (runnable == null) {
            throw new UnknownCommandException();
        }

        runnable.run();
    }
}
