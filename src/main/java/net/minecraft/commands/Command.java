package net.minecraft.commands;

import net.minecraft.commands.context.CommandContext;

public interface Command {
    void run(CommandContext context);
}
