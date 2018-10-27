// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.tree.CommandNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.defaultLiteral;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherUsagesTest {
    private CommandDispatcher<Object> subject;
    @Mock
    private Object source;
    @Mock
    private Command<Object> command;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher<>();
        subject.register(
            literal("a")
                .then(
                    literal("1")
                        .then(literal("i").executes(command))
                        .then(literal("ii").executes(command))
                )
                .then(
                    literal("2")
                        .then(literal("i").executes(command))
                        .then(literal("ii").executes(command))
                )
        );
        subject.register(literal("b").then(literal("1").executes(command)));
        subject.register(literal("c").executes(command));
        subject.register(literal("d").requires(s -> false).executes(command));
        subject.register(
            literal("e")
                .executes(command)
                .then(
                    literal("1")
                        .executes(command)
                        .then(literal("i").executes(command))
                        .then(literal("ii").executes(command))
                )
        );
        subject.register(
            literal("f")
                .then(
                    literal("1")
                        .then(literal("i").executes(command))
                        .then(literal("ii").executes(command).requires(s -> false))
                )
                .then(
                    literal("2")
                        .then(literal("i").executes(command).requires(s -> false))
                        .then(literal("ii").executes(command))
                )
        );
        subject.register(
            literal("g")
                .executes(command)
                .then(literal("1").then(literal("i").executes(command)))
        );
        subject.register(
            literal("h")
                .executes(command)
                .then(literal("1").then(literal("i").executes(command)))
                .then(literal("2").then(literal("i").then(literal("ii").executes(command))))
                .then(literal("3").executes(command))
        );
        subject.register(
            literal("i")
                .executes(command)
                .then(literal("1").executes(command))
                .then(literal("2").executes(command))
        );
        subject.register(
            literal("j")
                .redirect(subject.getRoot())
        );
        subject.register(
            literal("k")
                .redirect(get("h"))
        );
        //default node tests
        subject.register(
            literal("l")
                //basic
                .then(defaultLiteral("1").executes(command))
                //two choices
                .then(literal("2")
                    .then(defaultLiteral("i").executes(command))
                    .then(literal("ii").executes(command)))
                //chained
                .then(literal("3").then(defaultLiteral("i").then(defaultLiteral("ii").executes(command))))
                //unexecutable default node
                .then(literal("4").then(defaultLiteral("i").then(literal("ii").executes(command))))
        );
    }

    private CommandNode<Object> get(final String command) {
        return Iterables.getLast(subject.parse(command, source).getContext().getNodes().stream().filter(n -> !n.getRange().isEmpty()).collect(Collectors.toList())).getNode();
    }

    private CommandNode<Object> get(final StringReader command) {
        return Iterables.getLast(subject.parse(command, source).getContext().getNodes().stream().filter(n -> !n.getRange().isEmpty()).collect(Collectors.toList())).getNode();
    }

    @Test
    public void testAllUsage_noCommands() throws Exception {
        subject = new CommandDispatcher<>();
        final String[] results = subject.getAllUsage(subject.getRoot(), source, true);
        assertThat(results, is(emptyArray()));
    }

    @Test
    public void testSmartUsage_noCommands() throws Exception {
        subject = new CommandDispatcher<>();
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(subject.getRoot(), source);
        assertThat(results.entrySet(), is(empty()));
    }

    @Test
    public void testAllUsage_root() throws Exception {
        final String[] results = subject.getAllUsage(subject.getRoot(), source, true);
        assertThat(results, equalTo(new String[]{
            "a 1 i",
            "a 1 ii",
            "a 2 i",
            "a 2 ii",
            "b 1",
            "c",
            "e",
            "e 1",
            "e 1 i",
            "e 1 ii",
            "f 1 i",
            "f 2 ii",
            "g",
            "g 1 i",
            "h",
            "h 1 i",
            "h 2 i ii",
            "h 3",
            "i",
            "i 1",
            "i 2",
            "j ...",
            "k -> h",
            "l",
            "l 1",
            "l 2",
            "l 2 i",
            "l 2 ii",
            "l 3",
            "l 3 i",
            "l 3 i ii",
            "l 4 i ii",
        }));
    }

    @Test
    public void testSmartUsage_root() throws Exception {
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(subject.getRoot(), source);
        assertThat(results, equalTo(ImmutableMap.builder()
            .put(get("a"), "a (1|2)")
            .put(get("b"), "b 1")
            .put(get("c"), "c")
            .put(get("e"), "e [1]")
            .put(get("f"), "f (1|2)")
            .put(get("g"), "g [1]")
            .put(get("h"), "h [1|2|3]")
            .put(get("i"), "i [1|2]")
            .put(get("j"), "j ...")
            .put(get("k"), "k -> h")
            .put(get("l"), "l [1|2|3|4]")
            .build()
        ));
    }

    @Test
    public void testSmartUsage_h() throws Exception {
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(get("h"), source);
        assertThat(results, equalTo(ImmutableMap.builder()
            .put(get("h 1"), "[1] i")
            .put(get("h 2"), "[2] i ii")
            .put(get("h 3"), "[3]")
            .build()
        ));
    }

    @Test
    public void testSmartUsage_offsetH() throws Exception {
        final StringReader offsetH = new StringReader("/|/|/h");
        offsetH.setCursor(5);

        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(get(offsetH), source);
        assertThat(results, equalTo(ImmutableMap.builder()
            .put(get("h 1"), "[1] i")
            .put(get("h 2"), "[2] i ii")
            .put(get("h 3"), "[3]")
            .build()
        ));
    }

    @Test
    public void testSmartUsage_l() throws Exception {
        final Map<CommandNode<Object>, String> results = subject.getSmartUsage(get("l"), source);
        assertThat(results, equalTo(ImmutableMap.builder()
            .put(get("l 1"), "1")
            .put(get("l 2"), "2 [i|ii]")
            .put(get("l 3"), "3 [i]")
            .put(get("l 4"), "4 i ii")
            .build()
        ));
    }
}