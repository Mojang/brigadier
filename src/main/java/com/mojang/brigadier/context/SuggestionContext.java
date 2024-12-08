// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNode;

public class SuggestionContext<S> {
    public final CommandContextBuilder<S> context;
    public final CommandNode<S> parent;
    public final int startPos;

    public SuggestionContext(CommandContextBuilder<S> context, CommandNode<S> parent, int startPos) {
        this.context = context;
        this.parent = parent;
        this.startPos = startPos;
    }
}
