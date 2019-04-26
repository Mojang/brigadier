package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.RecursiveCommand;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

final class RecursiveCommandReturnFrame<S, I> implements Frame<S> {

    private final CommandContext<S> context;
    private final I intermediate;
    private final RecursiveCommand<S, I> command;

    RecursiveCommandReturnFrame(CommandContext<S> context, I intermediate, RecursiveCommand<S, I> recursiveCommand) {
        this.command = recursiveCommand;
        this.context = context;
        this.intermediate = intermediate;
    }

    @Override
    public void expand(Deque<Frame<S>> waitlist, DispatchingState<S> result) throws CommandSyntaxException {
        try {
            final int value = command.finish(context, intermediate);
            result.addResult(value);
            result.getConsumer().onCommandComplete(context, true, value);
            result.addFork();
        } catch (final CommandSyntaxException ex) {
            result.getConsumer().onCommandComplete(context, false, 0);
            if (!result.isForked()) {
                throw ex;
            }
        }
    }
}
