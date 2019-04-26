package com.mojang.brigadier.dispatching;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Deque;

@FunctionalInterface
interface Frame<S> {

    void expand(Deque<Frame<S>> waitlist, DispatchingState<S> result) throws CommandSyntaxException;

}
