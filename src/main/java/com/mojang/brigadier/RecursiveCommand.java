package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface RecursiveCommand<S, I> extends Command<S> {

    @Deprecated
    @Override
    default int run(final CommandContext<S> context) throws CommandSyntaxException {
        return finish(context, start(context));
    }

    I start(final CommandContext<S> context) throws CommandSyntaxException;

    int finish(final CommandContext<S> context, I intermediate) throws CommandSyntaxException;
}
