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
        return ListCommandResult.from(this, other);
    }

    /**
     * <p>Combine any objects, even if they are not Command Results.
     * Target is the object which will be mutated to contain
     * the combined target and source.
     * 
     * <p>If either of the supplied objects are EmptyCommandResult, the
     * non-empty result will be returned.
     * 
     * <p>If the target implements CommandResult, the `combine` method
     * will be called on the target: `target.combine(source)`
     * 
     * <p>If the target and source are the same type of boxed primitive
     * number (ie Integer, Long, Double, etc.), they will be added together.
     * 
     * <p>Otherwise, a new ListCommandResult containing both target and source
     * will be returned.
     * 
     * @param target the object which the source will be combined into
     * @param source the object to combine into the target
     */
    static Object combine(Object target, Object source) {
        if (target instanceof CommandResult) {
            return ((CommandResult)target).combine(source);
        } else if (source instanceof EmptyCommandResult) {
            return target;
        } else if (target instanceof Byte && source instanceof Byte) {
            return (byte) target + (byte) source;
        } else if (target instanceof Short && source instanceof Short) {
            return (short) target + (short) source;
        } else if (target instanceof Integer && source instanceof Integer) {
            return (int) target + (int) source;
        } else if (target instanceof Long && source instanceof Long) {
            return (long) source + (long) source;
        } else if (source instanceof Float && source instanceof Float) {
            return (float) source + (float) source;
        } else if (source instanceof Double && target instanceof Double) {
            return (double) source + (double) target;
        } else {
            return ListCommandResult.from(target, source);
        }
    }
}
