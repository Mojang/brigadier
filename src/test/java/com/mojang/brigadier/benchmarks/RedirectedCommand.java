package com.mojang.brigadier.benchmarks;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
public class RedirectedCommand {
    private CommandDispatcher<Object> dispatcher;
    private ParseResults<Object> parse;

    public static void main(final String[] args) throws CommandSyntaxException {
        final RedirectedCommand command = new RedirectedCommand();
        command.setup();
        command.execute();
    }

    @Setup
    public void setup() {
        dispatcher = new CommandDispatcher<>();
        dispatcher.register(literal("command").executes(c -> 0));
        dispatcher.register(literal("redirect").redirect(dispatcher.getRoot()));
        parse = dispatcher.parse("redirect command", new Object());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void execute() throws CommandSyntaxException {
        dispatcher.execute(parse);
    }
}
