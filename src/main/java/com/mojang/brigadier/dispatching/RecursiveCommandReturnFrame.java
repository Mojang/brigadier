// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.RecursiveCommand;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

final class RecursiveCommandReturnFrame<S, I> implements Frame<S> {
    private final CommandContext<S> context;
    private final I intermediate;
    private final RecursiveCommand<S, I> command;

    RecursiveCommandReturnFrame(final CommandContext<S> context, final I intermediate, final RecursiveCommand<S, I> recursiveCommand) {
        this.command = recursiveCommand;
        this.context = context;
        this.intermediate = intermediate;
    }

    @Override
    public void expand(final Deque<Frame<S>> waitlist, final DispatchingState<S> state) throws CommandSyntaxException {
        try {
            final int value = command.finish(context, intermediate);
            state.addResult(value);
            state.getConsumer().onCommandComplete(context, true, value);
            state.addFork();
        } catch (final CommandSyntaxException ex) {
            state.getConsumer().onCommandComplete(context, false, 0);
            if (!state.isForked()) {
                throw ex;
            }
        }
    }
}
