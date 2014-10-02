package com.mojang.brigadier.exceptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.text.StrSubstitutor;

public class ParameterizedCommandExceptionType implements CommandExceptionType {
    private static final Joiner JOINER = Joiner.on(", ");

    private final String name;
    private final String message;
    private final String[] keys;

    public ParameterizedCommandExceptionType(String name, String message, String... keys) {
        this.name = name;
        this.message = message;
        this.keys = keys;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getErrorMessage(CommandException exception) {
        return new StrSubstitutor(exception.getData()).replace(message);
    }

    public CommandException create(Object... values) {
        if (values.length != keys.length) {
            throw new IllegalArgumentException("Invalid values! (Expected: " + JOINER.join(keys) + ")");
        }

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        for (int i = 0; i < keys.length; i++) {
            builder = builder.put(keys[i], values[i]);
        }

        return new CommandException(this, builder.build());
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
