package net.minecraft.commands.tree;

import com.google.common.collect.Lists;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

import java.util.List;

public abstract class CommandNode {
    private final Runnable executor;
    private final List<CommandNode> children = Lists.newArrayList();

    protected CommandNode(Runnable executor) {
        this.executor = executor;
    }

    public Runnable getExecutor() {
        return executor;
    }

    public List<CommandNode> getChildren() {
        return children;
    }

    public void addChild(CommandNode node) {
        children.add(node);
    }

    public abstract CommandNode parse(String command) throws IllegalArgumentSyntaxException, ArgumentValidationException;
}
