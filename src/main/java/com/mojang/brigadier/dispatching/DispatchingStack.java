// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.ArrayDeque;
import java.util.Deque;

public final class DispatchingStack<S> {
    private final Deque<Frame<S>> stack = new ArrayDeque<>();
    private final Deque<Frame<S>> waitlist = new ArrayDeque<>();
    private DispatchingState<S> currentState = new DispatchingState<>((a, b, c) -> {
    });
    private boolean executing;

    public DispatchingStack() {
        this.executing = false;
    }

    DispatchingState<S> getCurrentState() {
        return this.currentState;
    }

    void setCurrentState(final DispatchingState<S> state) {
        this.currentState = state;
    }

    public DispatchingState<S> addCommand(final ParseResults<S> parse, final ResultConsumer<S> consumer) throws CommandSyntaxException {
        if (parse.getReader().canRead()) {
            if (parse.getExceptions().size() == 1) {
                throw parse.getExceptions().values().iterator().next();
            } else if (parse.getContext().getRange().isEmpty()) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
            } else {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
            }
        }

        final DispatchingState<S> collectingState = new DispatchingState<>(consumer);
        waitlist.addLast(new ParsedResultFrame<>(parse, collectingState, this));

        return collectingState;
    }

    public boolean isExecuting() {
        return this.executing;
    }

    public void execute() throws CommandSyntaxException {
        if (this.executing)
            return;
        this.executing = true;
        while (true) {
            while (!waitlist.isEmpty()) {
                stack.addFirst(waitlist.removeLast());
            }

            if (stack.isEmpty()) {
                break;
            }

            final Frame<S> frame = stack.removeFirst();
            try {
                frame.expand(waitlist, currentState);
            } catch (final CommandSyntaxException ex) {
                waitlist.clear();
                CommandSyntaxException currentException = ex;
                while (!stack.isEmpty() && currentException != null) {
                    final Frame<S> top = stack.removeFirst();
                    if (top instanceof ExceptionHandlerFrame) {
                        try {
                            ((ExceptionHandlerFrame<S>) top).handleException(ex, currentState);
                            currentException = null;
                        } catch (final CommandSyntaxException ex2) {
                            currentException = ex2;
                        }
                    }
                }

                if (currentException != null) {
                    throw currentException;
                }
            }
        }
        this.executing = false;
    }
}
