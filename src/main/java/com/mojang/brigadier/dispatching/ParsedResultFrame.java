// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

final class ParsedResultFrame<S> implements Frame<S> {
    private final ParseResults<S> parse;
    private final DispatchingState<S> collectingState;
    private final DispatchingStack<S> stack;

    ParsedResultFrame(final ParseResults<S> parse, final DispatchingState<S> state, final DispatchingStack<S> stack) {
        this.parse = parse;
        this.collectingState = state;
        this.stack = stack;
    }

    @Override
    public void expand(final Deque<Frame<S>> waitlist, final DispatchingState<S> state) throws CommandSyntaxException {
        final String command = parse.getReader().getString();
        final CommandContext<S> original = parse.getContext().build(command);

        final DispatchingState<S> old = stack.getCurrentState();
        stack.setCurrentState(collectingState);
        waitlist.addLast(new CommandContextFrame<>(original));
        waitlist.addLast(new StateCollectionFrame<>(original, parse.getReader(), stack, old));
    }
}
