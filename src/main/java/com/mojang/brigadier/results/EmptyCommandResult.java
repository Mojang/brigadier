// Copyright (c) Serena Lynas. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.results;

/**
 * Empty class which semantically represents
 * an empty command result.
 */
public class EmptyCommandResult implements CommandResult {
    /**
     * Combines this result with another. Always overwrites
     * the empty result.
     */
    @Override
    public Object combine(final Object other) {
        return other;
    }
}
