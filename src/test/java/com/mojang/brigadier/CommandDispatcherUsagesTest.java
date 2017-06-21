package com.mojang.brigadier;

import com.mojang.brigadier.exceptions.CommandException;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherUsagesTest {
    private CommandDispatcher<Object> subject;
    @Mock
    private Object source;
    @Mock
    private Command command;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher<>();
    }

    @Test
    public void testUnknownCommand() throws Exception {
        try {
            subject.getUsage("foo", source);
            fail();
        } catch (CommandException ex) {
            assertThat(ex.getType(), is(CommandDispatcher.ERROR_UNKNOWN_COMMAND));
            assertThat(ex.getData(), is(Collections.<String, Object>emptyMap()));
        }
    }

    @Test
    public void testSubcommandUsage() throws Exception {
        subject.register(
            literal("base").then(
                literal("foo").executes(command)
            ).then(
                literal("bar").then(
                    literal("baz").executes(command)
                ).then(
                    literal("qux").then(
                        literal("not_runnable")
                    )
                ).then(
                    literal("quux").then(
                        literal("corge").executes(command)
                    )
                ).executes(command)
            ).executes(command)
        );

        assertThat(subject.getUsage("base bar", source), hasToString("base bar [baz|quux]"));
    }

    @Test
    public void testOptionalSingleLiteral() throws Exception {
        subject.register(
            literal("base").then(
                literal("foo").executes(command)
            ).executes(command)
        );

        assertThat(subject.getUsage("base", source), hasToString("base [foo]"));
    }

    @Test
    public void testNoArguments() throws Exception {
        subject.register(
            literal("base").executes(command)
        );

        assertThat(subject.getUsage("base", source), hasToString("base"));
    }

    @Test
    public void testRequiredSingleLiteral() throws Exception {
        subject.register(
            literal("base").then(
                literal("foo").executes(command)
            )
        );

        assertThat(subject.getUsage("base", source), hasToString("base foo"));
    }

    @Test
    public void testOptionalTwoLiterals() throws Exception {
        subject.register(
            literal("base").then(
                literal("foo").executes(command)
            ).then(
                literal("bar").executes(command)
            ).executes(command)
        );

        assertThat(subject.getUsage("base", source), hasToString("base [foo|bar]"));
    }

    @Test
    public void testRequiredTwoLiterals() throws Exception {
        subject.register(
            literal("base").then(
                literal("foo").executes(command)
            ).then(
                literal("bar").executes(command)
            )
        );

        assertThat(subject.getUsage("base", source), hasToString("base (foo|bar)"));
    }

    @Test
    public void testOptionalOneArgument() throws Exception {
        subject.register(
            literal("base").then(
                argument("foo", integer()).executes(command)
            ).executes(command)
        );

        assertThat(subject.getUsage("base", source), hasToString("base [<foo>]"));
    }

    @Test
    public void testRequiredOneArgument() throws Exception {
        subject.register(
            literal("base").then(
                argument("foo", integer()).executes(command)
            )
        );

        assertThat(subject.getUsage("base", source), hasToString("base <foo>"));
    }

    @Test
    public void testOptionalTwoArguments() throws Exception {
        subject.register(
            literal("base").then(
                argument("foo", integer()).executes(command)
            ).then(
                argument("bar", integer()).executes(command)
            ).executes(command)
        );

        assertThat(subject.getUsage("base", source), hasToString("base [<foo>|<bar>]"));
    }

    @Test
    public void testRequiredTwoArguments() throws Exception {
        subject.register(
            literal("base").then(
                argument("foo", integer()).executes(command)
            ).then(
                argument("bar", integer()).executes(command)
            )
        );

        assertThat(subject.getUsage("base", source), hasToString("base (<foo>|<bar>)"));
    }

    @Test
    public void testOptionalLiteralOrArgument() throws Exception {
        subject.register(
            literal("base").then(
                literal("foo").executes(command)
            ).then(
                argument("bar", integer()).executes(command)
            ).then(
                literal("baz").executes(command)
            ).executes(command)
        );

        assertThat(subject.getUsage("base", source), hasToString("base [foo|baz|<bar>]"));
    }

    @Test
    public void testRequiredLiteralOrArgument() throws Exception {
        subject.register(
            literal("base").then(
                literal("foo").executes(command)
            ).then(
                argument("bar", integer()).executes(command)
            ).then(
                literal("baz").executes(command)
            )
        );

        assertThat(subject.getUsage("base", source), hasToString("base (foo|baz|<bar>)"));
    }
}