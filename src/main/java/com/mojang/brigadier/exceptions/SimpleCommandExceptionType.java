package com.mojang.brigadier.exceptions;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.ImmutableStringReader;

import java.util.Map;

public class SimpleCommandExceptionType implements CommandExceptionType {
    private final String name;
    private final String message;

    public SimpleCommandExceptionType(final String name, final String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getErrorMessage(final Map<String, String> data) {
        return message;
    }

    public CommandSyntaxException create() {
        return new CommandSyntaxException(this, ImmutableMap.of());
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader) {
        return new CommandSyntaxException(this, ImmutableMap.of(), reader.getString(), reader.getCursor());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandExceptionType)) return false;

        final CommandExceptionType that = (CommandExceptionType) o;

        return getTypeName().equals(that.getTypeName());
    }

    @Override
    public int hashCode() {
        return getTypeName().hashCode();
    }

    @Override
    public String toString() {
        return message;
    }
}
