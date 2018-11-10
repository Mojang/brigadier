// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNode;

import java.util.Objects;

/**
 * Represents a {@link CommandNode} that was parsed from the input.
 *
 * @param <S> the type of the command source
 */
public class ParsedCommandNode<S> {

    private final CommandNode<S> node;

    private final StringRange range;

    /**
     * Creates a new {@link ParsedCommandNode} for the given node within the given string range.
     *
     * @param node the node that was parsed
     * @param range the string range in the input it was parsed from
     */
    public ParsedCommandNode(CommandNode<S> node, StringRange range) {
        this.node = node;
        this.range = range;
    }

    /**
     * Returns the node that was parsed.
     *
     * @return the node that was parsed
     */
    public CommandNode<S> getNode() {
        return node;
    }

    /**
     * Returns the range this command node spans in the input string.
     *
     * @return the range this command node spans in the input string
     */
    public StringRange getRange() {
        return range;
    }

    @Override
    public String toString() {
        return node + "@" + range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedCommandNode<?> that = (ParsedCommandNode<?>) o;
        return Objects.equals(node, that.node) &&
                Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, range);
    }
}
