// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.tree.CommandNode;

import java.util.Collection;

/**
 * A consumer that is notified of found ambiguities in the command tree.
 *
 * @param <S> the command source
 * @see CommandDispatcher#findAmbiguities
 */
@FunctionalInterface
public interface AmbiguityConsumer<S> {

    /**
     * Invoked when ambiguities are detected.
     *
     * @param parent the parent command of the command that is ambiguous
     * @param child the first command that is ambiguous
     * @param sibling the second command that is ambiguous
     * @param inputs the inputs for which they both matched and therefore were ambiguous
     */
    void ambiguous(final CommandNode<S> parent, final CommandNode<S> child, final CommandNode<S> sibling, final Collection<String> inputs);
}
