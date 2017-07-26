package com.mojang.brigadier.exceptions;

import java.util.Map;

public class CommandException extends Exception {
    public static boolean ENABLE_COMMAND_STACK_TRACES = true;

    private final CommandExceptionType type;
    private final Map<String, Object> data;

    public CommandException(final CommandExceptionType type, final Map<String, Object> data) {
        super(type.getTypeName(), null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.data = data;
    }

    @Override
    public String getMessage() {
        return type.getErrorMessage(this);
    }

    public CommandExceptionType getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
