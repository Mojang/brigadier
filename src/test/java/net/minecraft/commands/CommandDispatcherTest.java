package net.minecraft.commands;

import net.minecraft.commands.context.CommandContext;
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
    CommandDispatcher subject;
    @Mock Command command;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher();
    }

    @Test
    public void testCreateAndExecuteCommand() throws Exception {
        subject.register(literal("foo").executes(command));

        subject.execute("foo");
        verify(command).run(any(CommandContext.class));
    }

    @Test
    public void testCreateAndMergeCommands() throws Exception {
        subject.register(literal("base").then(literal("foo")).executes(command));
        subject.register(literal("base").then(literal("bar")).executes(command));

        subject.execute("base foo");
        subject.execute("base bar");
        verify(command, times(2)).run(any(CommandContext.class));
    }

    @Test(expected = UnknownCommandException.class)
    public void testExecuteUnknownCommand() throws Exception {
        subject.execute("foo");
    }

    @Test(expected = UnknownCommandException.class)
    public void testExecuteUnknownSubcommand() throws Exception {
        subject.register(literal("foo").executes(command));
        subject.execute("foo bar");
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

        subject.execute("foo b");
        verify(subCommand).run(any(CommandContext.class));
    }

    @Test(expected = UnknownCommandException.class)
    public void testExecuteInvalidSubcommand() throws Exception {
        subject.register(literal("foo").then(
            argument("bar", integer())
        ).executes(command));

        subject.execute("foo bar");
    }
}