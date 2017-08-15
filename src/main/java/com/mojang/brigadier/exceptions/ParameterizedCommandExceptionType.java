package com.mojang.brigadier.exceptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterizedCommandExceptionType implements CommandExceptionType {
    private static final Pattern PATTERN = Pattern.compile("\\$\\{(\\w+)}");
    private static final Joiner JOINER = Joiner.on(", ");

    private final String name;
    private final String message;
    private final String[] keys;

    public ParameterizedCommandExceptionType(final String name, final String message, final String... keys) {
        this.name = name;
        this.message = message;
        this.keys = keys;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getErrorMessage(final Map<String, String> data) {
        final Matcher matcher = PATTERN.matcher(message);
        final StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(data.get(matcher.group(1))));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public CommandException create(final Object... values) {
        return new CommandException(this, createMap(values));
    }

    public CommandException createWithContext(final ImmutableStringReader reader, final Object... values) {
        return new CommandException(this, createMap(values), reader.getString(), reader.getCursor());
    }

    public Map<String, String> createMap(final Object... values) {
        if (values.length != keys.length) {
            throw new IllegalArgumentException("Invalid values! (Expected: " + JOINER.join(keys) + ")");
        }

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (int i = 0; i < keys.length; i++) {
            builder = builder.put(keys[i], String.valueOf(values[i]));
        }
        return builder.build();
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
