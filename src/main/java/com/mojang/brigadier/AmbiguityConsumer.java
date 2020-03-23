// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.tree.CommandNode;

import java.util.Collection;

@FunctionalInterface
public interface AmbiguityConsumer<S, R> {
    void ambiguous(final CommandNode<S, R> parent, final CommandNode<S, R> child, final CommandNode<S, R> sibling, final Collection<String> inputs);
}
