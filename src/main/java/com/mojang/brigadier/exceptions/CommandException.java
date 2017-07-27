package com.mojang.brigadier.exceptions;

import java.util.Map;

public class CommandException extends Exception {
    public static final int CONTEXT_AMOUNT = 10;
    public static boolean ENABLE_COMMAND_STACK_TRACES = true;

    private final CommandExceptionType type;
    private final Map<String, String> data;
    private final String input;
    private final int cursor;

    public CommandException(final CommandExceptionType type, final Map<String, String> data) {
        super(type.getTypeName(), null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.data = data;
        this.input = null;
        this.cursor = -1;
    }

    public CommandException(final CommandExceptionType type, final Map<String, String> data, final String input, final int cursor) {
        super(type.getTypeName(), null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.data = data;
        this.input = input;
        this.cursor = cursor;
    }

    @Override
    public String getMessage() {
        String message = type.getErrorMessage(data);
        final String context = getContext();
        if (context != null) {
            message += " at position " + cursor + ": " + context;
        }
        return message;
    }

    public String getContext() {
        if (input == null || cursor < 0) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        final int cursor = Math.min(input.length(), this.cursor);

        if (cursor > CONTEXT_AMOUNT) {
            builder.append("...");
        }

        builder.append(input.substring(Math.max(0, cursor - CONTEXT_AMOUNT), cursor));
        builder.append("<--[HERE]");

        return builder.toString();
    }

    public CommandExceptionType getType() {
        return type;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getInput() {
        return input;
    }

    public int getCursor() {
        return cursor;
    }
}
