package net.minecraft.commands.builder;

public class CommandBuilder {
    private final String name;
    private Runnable executor;

    protected CommandBuilder(String name) {
        this.name = name;
    }

    public static CommandBuilder command(String name) {
        return new CommandBuilder(name);
    }

    public String getName() {
        return name;
    }

    public CommandBuilder executes(Runnable executor) {
        this.executor = executor;
        return this;
    }

    public Runnable getExecutor() {
        return executor;
    }
}
