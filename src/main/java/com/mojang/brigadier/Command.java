package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;

public interface Command {
    void run(CommandContext context);
}
