// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.benchmarks;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
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
public class ParsingBenchmarks {
    private CommandDispatcher<Object> subject;

    @Setup
    public void setup() {
        subject = new CommandDispatcher<>();
        subject.register(
            literal("a")
                .then(
                    literal("1")
                        .then(literal("i").executes(c -> 0))
                        .then(literal("ii").executes(c -> 0))
                )
                .then(
                    literal("2")
                        .then(literal("i").executes(c -> 0))
                        .then(literal("ii").executes(c -> 0))
                )
        );
        subject.register(literal("b").then(literal("1").executes(c -> 0)));
        subject.register(literal("c").executes(c -> 0));
        subject.register(literal("d").requires(s -> false).executes(c -> 0));
        subject.register(
            literal("e")
                .executes(c -> 0)
                .then(
                    literal("1")
                        .executes(c -> 0)
                        .then(literal("i").executes(c -> 0))
                        .then(literal("ii").executes(c -> 0))
                )
        );
        subject.register(
            literal("f")
                .then(
                    literal("1")
                        .then(literal("i").executes(c -> 0))
                        .then(literal("ii").executes(c -> 0).requires(s -> false))
                )
                .then(
                    literal("2")
                        .then(literal("i").executes(c -> 0).requires(s -> false))
                        .then(literal("ii").executes(c -> 0))
                )
        );
        subject.register(
            literal("g")
                .executes(c -> 0)
                .then(literal("1").then(literal("i").executes(c -> 0)))
        );
        final LiteralCommandNode<Object> h = subject.register(
            literal("h")
                .executes(c -> 0)
                .then(literal("1").then(literal("i").executes(c -> 0)))
                .then(literal("2").then(literal("i").then(literal("ii").executes(c -> 0))))
                .then(literal("3").executes(c -> 0))
        );
        subject.register(
            literal("i")
                .executes(c -> 0)
                .then(literal("1").executes(c -> 0))
                .then(literal("2").executes(c -> 0))
        );
        subject.register(
            literal("j")
                .redirect(subject.getRoot())
        );
        subject.register(
            literal("k")
                .redirect(h)
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void parse_a1i() {
        subject.parse("a 1 i", new Object());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void parse_c() {
        subject.parse("c", new Object());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void parse_k1i() {
        subject.parse("k 1 i", new Object());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void parse_() {
        subject.parse("c", new Object());
    }
}
