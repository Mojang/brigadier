// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

interface ExceptionHandlerFrame<S> extends Frame<S> {

    /**
     * Handles a command exception thrown in a deeper frame.
     *
     * <p>Note: {@link #expand(Deque, DispatchingState)} call will be skipped when the exception is handled.</p>
     *
     * @param ex the exception to handle
     * @param result the current dispatching state in the stack
     * @throws CommandSyntaxException if a new exception emerges or if this handler wants to rethrow the exception
     */
    void handleException(CommandSyntaxException ex, DispatchingState<S> result) throws CommandSyntaxException;
}
