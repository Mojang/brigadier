package net.minecraft.commands;

import net.minecraft.commands.exceptions.UnknownCommandException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.minecraft.commands.builder.CommandBuilder.command;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CommandDispatcherTest {
    CommandDispatcher subject;
    @Mock Runnable runnable;

    @Before
    public void setUp() throws Exception {
        subject = new CommandDispatcher();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateCommand() throws Exception {
        subject.register(command("foo").executes(runnable));
        subject.register(command("foo").executes(runnable));
    }

    @Test
    public void testCreateAndExecuteCommand() throws Exception {
        subject.register(command("foo").executes(runnable));

        subject.execute("foo");
        verify(runnable).run();
    }

    @Test(expected = UnknownCommandException.class)
    public void testExecuteUnknownCommand() throws Exception {
        subject.execute("foo");
    }
}