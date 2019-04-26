// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RecursiveCommand;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.Deque;

final class CommandContextFrame<S> implements Frame<S> {
    private final CommandContext<S> context;

    CommandContextFrame(final CommandContext<S> context) {
        this.context = context;
    }

    @Override
    public void expand(final Deque<Frame<S>> waitlist, final DispatchingState<S> state) throws CommandSyntaxException {
        final CommandContext<S> child = context.getChild();
        if (child != null) {
            if (context.isForked()) {
                state.setForked();
            }
            if (child.hasNodes()) {
                state.foundCommand();
                final RedirectModifier<S> modifier = context.getRedirectModifier();
                if (modifier == null) {
                    waitlist.add(new CommandContextFrame<>(child.copyFor(context.getSource())));
                } else {
                    try {
                        final Collection<S> results = modifier.apply(context);
                        if (!results.isEmpty()) {
                            for (final S source : results) {
                                waitlist.add(new CommandContextFrame<>(child.copyFor(source)));
                            }
                        }
                    } catch (final CommandSyntaxException ex) {
                        state.getConsumer().onCommandComplete(context, false, 0);
                        if (!state.isForked()) {
                            throw ex;
                        }
                    }
                }
            }
        } else if (context.getCommand() != null) {
            state.foundCommand();
            try {
                final Command<S> cmd = context.getCommand();
                if (cmd instanceof RecursiveCommand) {
                    addRecursiveCommand(waitlist, (RecursiveCommand<S, ?>) cmd);
                } else {
                    final int value = cmd.run(context);
                    state.addResult(value);
                    state.getConsumer().onCommandComplete(context, true, value);
                    state.addFork();
                }
            } catch (final CommandSyntaxException ex) {
                state.getConsumer().onCommandComplete(context, false, 0);
                if (!state.isForked()) {
                    throw ex;
                }
            }
        }
    }

    // bypasses generic limits
    private <I> void addRecursiveCommand(final Deque<Frame<S>> waitlist, final RecursiveCommand<S, I> command) throws CommandSyntaxException {
        waitlist.addLast(new RecursiveCommandReturnFrame<>(context, command.start(context), command));
    }
}
