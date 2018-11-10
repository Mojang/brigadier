// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * The functional interface actually representing a command to execute.
 * <p>
 * This interface represents the code that will be run, when the command is executed.
 *
 * @param <S> the type of the command source this command will be called with
 */
@FunctionalInterface
public interface Command<S> {
    /**
     * A static constant you can use when the command completed successfully.
     * <p>
     * As the return value means what you want it to mean, this is merely a suggestion to make the code a bit more
     * expressive, if you follow it.
     */
    int SINGLE_SUCCESS = 1;

    /**
     * Executes the command using the given {@link CommandContext} to supply information to the command.
     *
     * @param context the command context to use for supplying all information the command will need
     * @return the result of executing the command. The value is arbitrary, though {@link #SINGLE_SUCCESS} is a
     * suggestion
     * @throws CommandSyntaxException TODO: Why does it throw a <strong>Syntax</strong> exception here?
     */
    int run(CommandContext<S> context) throws CommandSyntaxException;
}
