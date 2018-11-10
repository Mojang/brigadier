// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * Basically a {@link RedirectModifier} (though no subtype) that only converts to a single command source-
 *
 * @param <S> the type of the command source
 */
@FunctionalInterface
public interface SingleRedirectModifier<S> {

    /**
     * Applies the redirect modifier to the command context, returning the new command source to use for invoking the
     * command.
     *
     * @param context the command context to base it on
     * @return the new command source to invoke the command for
     * @throws CommandSyntaxException if an error occurred
     */
    S apply(CommandContext<S> context) throws CommandSyntaxException;
}
