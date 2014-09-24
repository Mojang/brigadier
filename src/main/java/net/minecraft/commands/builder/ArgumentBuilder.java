package net.minecraft.commands.builder;

import com.google.common.collect.Lists;
import net.minecraft.commands.tree.CommandNode;

import java.util.List;

public abstract class ArgumentBuilder<T extends ArgumentBuilder<?>> {
    private final List<ArgumentBuilder> arguments = Lists.newArrayList();
    private Runnable executor;

    protected abstract T getThis();

    public T then(ArgumentBuilder argument) {
        arguments.add(argument);
        return getThis();
    }

    public List<ArgumentBuilder> getArguments() {
        return arguments;
    }

    public T executes(Runnable executor) {
        this.executor = executor;
        return getThis();
    }

    public Runnable getExecutor() {
        return executor;
    }

    public abstract CommandNode build();
}
