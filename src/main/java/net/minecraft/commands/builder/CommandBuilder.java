package net.minecraft.commands.builder;

public abstract class CommandBuilder {
    private boolean finished;
    private Runnable commandExecutor;

    public void finish() {
        if (finished) {
            throw new IllegalStateException("Cannot finish() multiple times!");
        }
        if (commandExecutor == null) {
            throw new IllegalStateException("Cannot finish() without a command executor!");
        }
        onFinish();
        finished = true;
    }

    protected abstract void onFinish();

    public CommandBuilder executes(Runnable runnable) {
        this.commandExecutor = runnable;
        return this;
    }

    public Runnable getCommandExecutor() {
        return commandExecutor;
    }
}
