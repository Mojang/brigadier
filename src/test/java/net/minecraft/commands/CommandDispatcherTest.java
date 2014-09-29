package net.minecraft.commands;

import net.minecraft.commands.context.CommandContext;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;
import net.minecraft.commands.exceptions.UnknownCommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.minecraft.commands.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.builder.LiteralArgumentBuilder.literal;
import static net.minecraft.commands.builder.RequiredArgumentBuilder.argument;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherTest {
    CommandDispatcher<Object> subject;
    @Mock Command command;
    @Mock Object source;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher<Object>();
    }

    @Test
    public void testCreateAndExecuteCommand() throws Exception {
        subject.register(literal("foo").executes(command));

        subject.execute("foo", source);
        verify(command).run(any(CommandContext.class));
    }

    @Test
    public void testCreateAndMergeCommands() throws Exception {
        subject.register(literal("base").then(literal("foo")).executes(command));
        subject.register(literal("base").then(literal("bar")).executes(command));

        subject.execute("base foo", source);
        subject.execute("base bar", source);
        verify(command, times(2)).run(any(CommandContext.class));
    }

    @Test
    public void testCreateAndExecuteOverlappingCommands() throws Exception {
        Command one = mock(Command.class);
        Command two = mock(Command.class);
        Command three = mock(Command.class);

        subject.register(
            literal("foo").then(
                argument("one", integer()).then(
                    literal("one").executes(one)
                )
            ).then(
                argument("two", integer()).then(
                    literal("two").executes(two)
                )
            ).then(
                argument("three", integer()).then(
                    literal("three").executes(three)
                )
            )
        );

        subject.execute("foo 1 one", source);
        verify(one).run(any(CommandContext.class));

        subject.execute("foo 2 two", source);
        verify(two).run(any(CommandContext.class));

        subject.execute("foo 3 three", source);
        verify(three).run(any(CommandContext.class));
    }

    @Test(expected = UnknownCommandException.class)
    public void testExecuteUnknownCommand() throws Exception {
        subject.execute("foo", source);
    }

    @Test(expected = UnknownCommandException.class)
    public void testExecuteUnknownSubcommand() throws Exception {
        subject.register(literal("foo").executes(command));
        subject.execute("foo bar", source);
    }

    @Test
    public void testExecuteSubcommand() throws Exception {
        Command subCommand = mock(Command.class);

        subject.register(literal("foo").then(
            literal("a")
        ).then(
            literal("b").executes(subCommand)
        ).then(
            literal("c")
        ).executes(command));

        subject.execute("foo b", source);
        verify(subCommand).run(any(CommandContext.class));
    }

    @Test(expected = IllegalArgumentSyntaxException.class)
    public void testExecuteInvalidSubcommand() throws Exception {
        subject.register(literal("foo").then(
            argument("bar", integer())
        ).executes(command));

        subject.execute("foo bar", source);
    }
}