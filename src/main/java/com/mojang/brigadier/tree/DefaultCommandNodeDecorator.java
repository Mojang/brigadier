// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.tree;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class DefaultCommandNodeDecorator<S, T> extends CommandNodeDecorator<S> {
    private final T defaultValue;

    public DefaultCommandNodeDecorator(final CommandNodeInterface<S> delegate, final T defaultValue) {
        super(delegate);
        if(delegate instanceof RootCommandNode) throw new IllegalArgumentException("Root command node cannot have a default value");
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        if(reader.canRead())
            super.parse(reader, contextBuilder);

        if(defaultValue != null) {
            final ParsedArgument<S, T> parsed = new ParsedArgument<>(reader.getCursor(), reader.getCursor(), defaultValue);
            contextBuilder.withArgument(getName(), parsed);
        }
        contextBuilder.withNode(this, StringRange.at(reader.getCursor()));
    }
}
