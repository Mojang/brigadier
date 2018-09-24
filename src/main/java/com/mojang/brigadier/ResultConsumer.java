// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;

@FunctionalInterface
public interface ResultConsumer<S> {
    void onCommandComplete(CommandContext<S> context, boolean success, int result);
}
