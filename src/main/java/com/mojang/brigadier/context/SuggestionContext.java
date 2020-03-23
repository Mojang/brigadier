// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNode;

public class SuggestionContext<S, R> {
    public final CommandNode<S, R> parent;
    public final int startPos;

    public SuggestionContext(CommandNode<S, R> parent, int startPos) {
        this.parent = parent;
        this.startPos = startPos;
    }
}
