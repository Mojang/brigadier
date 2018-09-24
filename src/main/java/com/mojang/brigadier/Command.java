// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface Command<S> {
    int SINGLE_SUCCESS = 1;

    int run(CommandContext<S> context) throws CommandSyntaxException;
}
