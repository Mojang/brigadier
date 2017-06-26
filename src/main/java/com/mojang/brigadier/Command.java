package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;

public interface Command<S> {
    void run(CommandContext<S> context);
}
