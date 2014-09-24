package net.minecraft.commands;

import net.minecraft.commands.context.CommandContext;
import net.minecraft.commands.exceptions.UnknownCommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.minecraft.commands.builder.LiteralArgumentBuilder.literal;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherTest {
    CommandDispatcher subject;
    @Mock Command command;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateCommand() throws Exception {
        subject.register(literal("foo").executes(command));
        subject.register(literal("foo").executes(command));
    }

    @Test
    public void testCreateAndExecuteCommand() throws Exception {
        subject.register(literal("foo").executes(command));

        subject.execute("foo");
        verify(command).run(any(CommandContext.class));
    }

    @Test(expected = UnknownCommandException.class)
    public void testExecuteUnknownCommand() throws Exception {
        subject.execute("foo");
    }
}