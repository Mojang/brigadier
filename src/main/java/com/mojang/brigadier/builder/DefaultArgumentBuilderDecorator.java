// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNodeInterface;
import com.mojang.brigadier.tree.DefaultCommandNodeDecorator;

public class DefaultArgumentBuilderDecorator<S, T> extends ArgumentBuilderDecorator<S, DefaultArgumentBuilderDecorator<S, T>> {
    private final T defaultValue;

    DefaultArgumentBuilderDecorator(final ArgumentBuilderInterface<S, ?> delegate, final T defaultValue) {
        super(delegate);
        this.defaultValue = defaultValue;
    }

    @Override
    protected DefaultArgumentBuilderDecorator<S, T> getThis() {
        return this;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public CommandNodeInterface<S> build() {
        return new DefaultCommandNodeDecorator<>(super.build(), defaultValue);
    }
}
