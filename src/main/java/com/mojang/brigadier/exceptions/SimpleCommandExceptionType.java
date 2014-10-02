package com.mojang.brigadier.exceptions;

import com.google.common.collect.ImmutableMap;

public class SimpleCommandExceptionType implements CommandExceptionType {
    private final String name;
    private final String message;

    public SimpleCommandExceptionType(String name, String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getErrorMessage(CommandException exception) {
        return message;
    }

    public CommandException create() {
        return new CommandException(this, ImmutableMap.<String, Object>of());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandExceptionType)) return false;

        CommandExceptionType that = (CommandExceptionType) o;

        return getTypeName().equals(that.getTypeName());
    }

    @Override
    public int hashCode() {
        return getTypeName().hashCode();
    }
}
