// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

final class StateCollectionFrame<S> implements ExceptionHandlerFrame<S> {
    private final CommandContext<S> original;
    private final ImmutableStringReader reader;
    private final DispatchingStack<S> stack;
    private final DispatchingState<S> lastState;

    StateCollectionFrame(final CommandContext<S> original, final ImmutableStringReader reader, final DispatchingStack<S> stack, final DispatchingState<S> lastState) {
        this.original = original;
        this.reader = reader;
        this.stack = stack;
        this.lastState = lastState;
    }

    @Override
    public void expand(final Deque<Frame<S>> waitlist, final DispatchingState<S> state) throws CommandSyntaxException {
        stack.setCurrentState(lastState);
        if (!state.hasFoundCommands()) {
            state.getConsumer().onCommandComplete(original, false, 0);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(reader);
        }
    }

    @Override
    public void handleException(final CommandSyntaxException ex, final DispatchingState<S> result) {
        result.setException(ex);
        stack.setCurrentState(lastState);
    }
}
