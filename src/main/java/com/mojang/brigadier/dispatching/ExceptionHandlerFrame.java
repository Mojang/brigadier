package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface ExceptionHandlerFrame<S> extends Frame<S> {

    void handleException(CommandSyntaxException ex, DispatchingState<S> result) throws CommandSyntaxException;
}
