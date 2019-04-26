package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public final class DispatchingState<S> {
    private int result = 0;
    private int successfulForks = 0;
    private boolean forked = false;
    private boolean foundCommands = false;
    private final ResultConsumer<S> consumer;
    private CommandSyntaxException exception;

    DispatchingState(ResultConsumer<S> consumer) {
        this.consumer = consumer;
    }

    public void addResult(int result) {
        this.result += result;
    }

    public void setForked() {
        this.forked = true;
    }

    public void addFork() {
        this.successfulForks++;
    }

    public void foundCommand() {
        this.foundCommands = true;
    }

    public int getReturnValue() {
        return forked ? successfulForks : result;
    }

    public int getResult() {
        return result;
    }

    public boolean isForked() {
        return forked;
    }

    public boolean hasFoundCommands() {
        return foundCommands;
    }

    public ResultConsumer<S> getConsumer() {
        return this.consumer;
    }

    public void setException(CommandSyntaxException ex) {
        this.exception = ex;
    }

    public CommandSyntaxException getException() {
        return this.exception;
    }
}
