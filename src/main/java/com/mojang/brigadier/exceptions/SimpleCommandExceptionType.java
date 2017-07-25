package com.mojang.brigadier.exceptions;

import com.google.common.collect.ImmutableMap;

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
    public String getErrorMessage(final CommandException exception) {
        return message;
    }

    public CommandException create() {
        return new CommandException(this, ImmutableMap.of());
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
}
