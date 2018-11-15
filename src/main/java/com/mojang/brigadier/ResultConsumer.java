// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;

/**
 * A consumer that is called by the {@link CommandDispatcher} whenever a command completed.
 *
 * @param <S> the type of the command source
 */
@FunctionalInterface
public interface ResultConsumer<S> {
    /**
     * Invoked when a command execution completed, either normally or abnormally.
     *
     * @param context the command context of the execution
     * @param success whether the command completed successfully
     * @param result the result of the command, if it completed successfully. If not, the meaning of the result is not
     * defined.
     */
    void onCommandComplete(CommandContext<S> context, boolean success, int result);
}
