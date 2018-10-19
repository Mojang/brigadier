// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNodeInterface;

import java.util.Objects;

public class ParsedCommandNode<S> {

    private final CommandNodeInterface<S> node;

    private final StringRange range;

    public ParsedCommandNode(CommandNodeInterface<S> node, StringRange range) {
        this.node = node;
        this.range = range;
    }

    public CommandNodeInterface<S> getNode() {
        return node;
    }

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
