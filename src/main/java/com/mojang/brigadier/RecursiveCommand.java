package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface RecursiveCommand<S, I> extends Command<S> {

    @Override
    default int run(CommandContext<S> context) throws CommandSyntaxException {
        return finish(context, start(context));
    }

    I start(CommandContext<S> context) throws CommandSyntaxException;

    int finish(CommandContext<S> context, I intermediate) throws CommandSyntaxException;
}
