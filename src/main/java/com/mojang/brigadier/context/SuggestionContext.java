// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNodeInterface;

public class SuggestionContext<S> {
    public final CommandNodeInterface<S> parent;
    public final int startPos;

    public SuggestionContext(CommandNodeInterface<S> parent, int startPos) {
        this.parent = parent;
        this.startPos = startPos;
    }
}
