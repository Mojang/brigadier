// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNode;

/**
 * Represents a context for a suggestion, just encompassing a command node and where the completion start.
 *
 * @param <S> the type of the command source
 */
public class SuggestionContext<S> {
    public final CommandNode<S> parent;
    public final int startPos;

    /**
     * Creates a new SuggestionContext with the given command node and start position.
     *
     * @param parent the node that handles the completion
     * @param startPos the starting position where it should complete from
     */
    public SuggestionContext(CommandNode<S> parent, int startPos) {
        this.parent = parent;
        this.startPos = startPos;
    }
}
