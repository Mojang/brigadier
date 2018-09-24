// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.benchmarks;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

@State(Scope.Benchmark)
public class ExecuteBenchmarks {
    private CommandDispatcher<Object> dispatcher;
    private ParseResults<Object> simple;
    private ParseResults<Object> singleRedirect;
    private ParseResults<Object> forkedRedirect;

    @Setup
    public void setup() {
        dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("command").executes(c -> 0));
        dispatcher.register(literal("redirect").redirect(dispatcher.getRoot()));
        dispatcher.register(literal("fork").fork(dispatcher.getRoot(), o -> Lists.newArrayList(new Object(), new Object(), new Object())));
        simple = dispatcher.parse("command", new Object());
        singleRedirect = dispatcher.parse("redirect command", new Object());
        forkedRedirect = dispatcher.parse("fork command", new Object());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void execute_simple() throws CommandSyntaxException {
        dispatcher.execute(simple);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void execute_single_redirect() throws CommandSyntaxException {
        dispatcher.execute(singleRedirect);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void execute_forked_redirect() throws CommandSyntaxException {
        dispatcher.execute(forkedRedirect);
    }
}
