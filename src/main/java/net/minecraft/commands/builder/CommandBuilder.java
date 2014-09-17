package net.minecraft.commands.builder;

import com.google.common.collect.Lists;
import net.minecraft.commands.tree.LiteralCommandNode;

import java.util.List;

public class CommandBuilder {
    private final String name;
    private final List<ArgumentBuilder> arguments = Lists.newArrayList();
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

    public CommandBuilder then(ArgumentBuilder argument) {
        arguments.add(argument);
        return this;
    }

    public List<ArgumentBuilder> getArguments() {
        return arguments;
    }

    public LiteralCommandNode build() {
        LiteralCommandNode result = new LiteralCommandNode(getName());

        for (ArgumentBuilder argument : arguments) {
            result.addChild(argument.build());
        }

        return result;
    }
}
