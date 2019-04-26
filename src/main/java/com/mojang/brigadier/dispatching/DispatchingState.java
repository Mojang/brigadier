// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * A cumulative dispatching result that is in progress of collection.
 *
 * @param <S> the command source type
 */
public final class DispatchingState<S> {
    private int result = 0;
    private int successfulForks = 0;
    private boolean forked = false;
    private boolean foundCommands = false;
    private CommandSyntaxException exception;
    private final ResultConsumer<S> consumer;

    DispatchingState(final ResultConsumer<S> consumer) {
        this.consumer = consumer;
    }

    /**
     * Gets the current return value of the dispatching state.
     *
     * @return the current result of the dispatching state
     * @throws CommandSyntaxException if the state has observed an exception
     */
    public int getReturnValue() throws CommandSyntaxException {
        if (exception != null) {
            throw exception;
        }
        return forked ? successfulForks : result;
    }

    void addResult(final int result) {
        this.result += result;
    }

    void setForked() {
        this.forked = true;
    }

    void addFork() {
        this.successfulForks++;
    }

    void foundCommand() {
        this.foundCommands = true;
    }

    int getResult() {
        return result;
    }

    boolean isForked() {
        return forked;
    }

    boolean hasFoundCommands() {
        return foundCommands;
    }

    ResultConsumer<S> getConsumer() {
        return this.consumer;
    }

    void setException(final CommandSyntaxException ex) {
        this.exception = ex;
    }

    CommandSyntaxException getException() {
        return this.exception;
    }
}
