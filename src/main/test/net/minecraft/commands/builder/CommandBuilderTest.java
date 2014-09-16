package net.minecraft.commands.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommandBuilderTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS) CommandBuilder builder;
    @Mock Runnable commandExecutor;

    @Before
    public void setUp() throws Exception {
        Mockito.doNothing().when(builder).onFinish();
    }

    @Test
    public void testFinish() throws Exception {
        builder.executes(commandExecutor);
        builder.finish();

        Mockito.verify(builder).onFinish();
    }

    @Test(expected = IllegalStateException.class)
    public void testFinishTwice() throws Exception {
        builder.executes(commandExecutor);
        builder.finish();
        builder.finish();
    }

    @Test(expected = IllegalStateException.class)
    public void testFinishWithoutCommandExecutor() throws Exception {
        builder.finish();
    }
}