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

    CommandContextFrame(CommandContext<S> context) {
        this.context = context;
    }

    @Override
    public void expand(Deque<Frame<S>> waitlist, DispatchingState<S> result) throws CommandSyntaxException {
        final CommandContext<S> child = context.getChild();
        if (child != null) {
            if (context.isForked()) {
                result.setForked();
            }
            if (child.hasNodes()) {
                result.foundCommand();
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
                        result.getConsumer().onCommandComplete(context, false, 0);
                        if (!result.isForked()) {
                            throw ex;
                        }
                    }
                }
            }
        } else if (context.getCommand() != null) {
            result.foundCommand();
            try {
                final Command<S> cmd = context.getCommand();
                if (cmd instanceof RecursiveCommand) {
                    addRecursiveCommand(waitlist, (RecursiveCommand<S, ?>) cmd);
                } else {
                    final int value = cmd.run(context);
                    result.addResult(value);
                    result.getConsumer().onCommandComplete(context, true, value);
                    result.addFork();
                }
            } catch (final CommandSyntaxException ex) {
                result.getConsumer().onCommandComplete(context, false, 0);
                if (!result.isForked()) {
                    throw ex;
                }
            }
        }
    }

    // bypasses generic limits
    private <I> void addRecursiveCommand(Deque<Frame<S>> waitlist, RecursiveCommand<S, I> command) throws CommandSyntaxException {
        waitlist.addLast(new RecursiveCommandReturnFrame<>(context, command.start(context), command));
    }
}
