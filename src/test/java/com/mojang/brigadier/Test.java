package com.mojang.brigadier;

import java.util.Arrays;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class Test {
    public static void main(String[] args) {
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();

        dispatcher.register(literal("foo").executes(ctx -> 1).thenDefault(argument("bar", integer()).then(argument("baz", integer()).executes(ctx -> 1)), ""));

        System.out.println(Arrays.toString(dispatcher.getAllUsage(dispatcher.getRoot(), null, false)));
    }
}
