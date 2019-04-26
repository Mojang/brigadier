// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

@FunctionalInterface
interface Frame<S> {

    /**
     * Executes the frame and add more frames as necessary.
     *
     * <p>When adding frames, please add to the end of the waitlist.</p>
     *
     * @param waitlist the collection to add more frames to
     * @param state the current dispatching state
     * @throws CommandSyntaxException if an exception emerges during execution
     */
    void expand(Deque<Frame<S>> waitlist, DispatchingState<S> state) throws CommandSyntaxException;

}
