// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CommandNode<S> implements CommandNodeInterface<S> {
    private Map<String, CommandNodeInterface<S>> children = new LinkedHashMap<>();
    private Map<String, LiteralCommandNode<S>> literals = new LinkedHashMap<>();
    private Map<String, ArgumentCommandNode<S, ?>> arguments = new LinkedHashMap<>();
    private final Predicate<S> requirement;
    private final DefaultCommandNodeDecorator<S, ?> defaultNode;
    private final CommandNodeInterface<S> redirect;
    private final RedirectModifier<S> modifier;
    private final boolean forks;
    private Command<S> command;

    protected CommandNode(final Command<S> command, final Predicate<S> requirement, final DefaultCommandNodeDecorator<S, ?> defaultNode, final CommandNodeInterface<S> redirect, final RedirectModifier<S> modifier, final boolean forks) {
        this.command = command;
        this.requirement = requirement;
        this.defaultNode = defaultNode;
        this.redirect = redirect;
        this.modifier = modifier;
        this.forks = forks;
    }

    @Override
    public Command<S> getCommand() {
        return command;
    }

    @Override
    public Collection<CommandNodeInterface<S>> getChildren() {
        return children.values();
    }

    @Override
    public CommandNodeInterface<S> getChild(final String name) {
        return children.get(name);
    }

    @Override
    public CommandNodeInterface<S> getRedirect() {
        return redirect;
    }

    @Override
    public RedirectModifier<S> getRedirectModifier() {
        return modifier;
    }

    @Override
    public DefaultCommandNodeDecorator<S, ?> getDefaultNode() {
        return defaultNode;
    }

    @Override
    public boolean canUse(final S source) {
        return requirement.test(source);
    }

    @Override
    public void addChild(final CommandNodeInterface<S> node) {
        if (node instanceof RootCommandNode) {
            throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNodeInterface");
        }

        final CommandNodeInterface<S> child = children.get(node.getName());
        if (child != null) {
            // We've found something to merge onto
            final CommandNodeInterface<S> base = child.getUndecoratedNode();

            if (base instanceof CommandNode && node.getCommand() != null) {
                ((CommandNode<S>)base).command = node.getCommand();
            }
            for (final CommandNodeInterface<S> grandchild : node.getChildren()) {
                base.addChild(grandchild);
            }
        } else {
            children.put(node.getName(), node);
            final CommandNodeInterface<S> base = node.getUndecoratedNode();
            if (base instanceof LiteralCommandNode) {
                literals.put(base.getName(), (LiteralCommandNode<S>) base);
            } else if (base instanceof ArgumentCommandNode) {
                arguments.put(base.getName(), (ArgumentCommandNode<S, ?>) base);
            }
        }

        children = children.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public void findAmbiguities(final AmbiguityConsumer<S> consumer) {
        Set<String> matches = new HashSet<>();

        for (final CommandNodeInterface<S> child : children.values()) {
            for (final CommandNodeInterface<S> sibling : children.values()) {
                if (child == sibling) {
                    continue;
                }

                for (final String input : child.getExamples()) {
                    if (sibling.isValidInput(input)) {
                        matches.add(input);
                    }
                }

                if (matches.size() > 0) {
                    consumer.ambiguous(this, child, sibling, matches);
                    matches = new HashSet<>();
                }
            }

            child.findAmbiguities(consumer);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandNode)) return false;

        final CommandNode<?> that = (CommandNode<?>) o;

        if (!children.equals(that.children)) return false;
        if (command != null ? !command.equals(that.command) : that.command != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * children.hashCode() + (command != null ? command.hashCode() : 0);
    }

    @Override
    public Predicate<S> getRequirement() {
        return requirement;
    }

    protected abstract String getSortedKey();

    @Override
    public Collection<? extends CommandNodeInterface<S>> getRelevantNodes(final StringReader input) {
        if(!input.canRead() && defaultNode != null) {
            return Collections.singleton(defaultNode);
        } else if (literals.size() > 0) {
            final int cursor = input.getCursor();
            while (input.canRead() && input.peek() != ' ') {
                input.skip();
            }
            final String text = input.getString().substring(cursor, input.getCursor());
            input.setCursor(cursor);
            final LiteralCommandNode<S> literal = literals.get(text);
            if (literal != null) {
                return Collections.singleton(literal);
            } else {
                return arguments.values();
            }
        } else {
            return arguments.values();
        }
    }

    @Override
    public boolean isFork() {
        return forks;
    }

}
