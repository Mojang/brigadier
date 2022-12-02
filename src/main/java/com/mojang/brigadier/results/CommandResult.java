// Copyright (c) Serena Lynas. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.results;

/**
 * Optional interface for CommandResult
 * 
 * Not all things returned from commands
 * must implement this interface.
 */
public interface CommandResult {
    /**
     * Combine one command result with another, returning
     * the combined result.
     * @param other The other result to combine this result
     * with.
     * @return The combined result
     */
    default Object combine(Object other) {
        ListCommandResult list = new ListCommandResult();
        list.combine(this);
        list.combine(other);
        return list;
    }

    static Object combine(Object target, Object source) {
        if (target instanceof CommandResult) {
            return ((CommandResult)target).combine(source);
        } else if (target instanceof Integer && source instanceof Integer) {
            // Backwards compatability
            return (Integer)target + (Integer)source;
        } else if (source instanceof EmptyCommandResult) {
            return target;
        } else {
            return source;
        }
    }
}
