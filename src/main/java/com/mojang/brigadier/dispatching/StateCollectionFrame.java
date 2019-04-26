// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

final class StateCollectionFrame<S> implements ExceptionHandlerFrame<S> {

    private final CommandContext<S> original;
    private final ImmutableStringReader reader;
    private final DispatchingStack<S> stack;
    private final DispatchingState<S> lastState;

    StateCollectionFrame(CommandContext<S> original, ImmutableStringReader reader, DispatchingStack<S> stack, DispatchingState<S> lastState) {
        this.original = original;
        this.reader = reader;
        this.stack = stack;
        this.lastState = lastState;
    }

    @Override
    public void expand(Deque<Frame<S>> waitlist, DispatchingState<S> state) throws CommandSyntaxException {
        stack.setCurrentState(lastState);
        if (!state.hasFoundCommands()) {
            state.getConsumer().onCommandComplete(original, false, 0);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(reader);
        }
    }

    @Override
    public void handleException(CommandSyntaxException ex, DispatchingState<S> result) throws CommandSyntaxException {
        result.setException(ex);
        stack.setCurrentState(lastState);
    }
}
