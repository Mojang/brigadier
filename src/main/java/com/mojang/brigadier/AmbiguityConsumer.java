// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.tree.CommandNodeInterface;

import java.util.Collection;

@FunctionalInterface
public interface AmbiguityConsumer<S> {
    void ambiguous(final CommandNodeInterface<S> parent, final CommandNodeInterface<S> child, final CommandNodeInterface<S> sibling, final Collection<String> inputs);
}
