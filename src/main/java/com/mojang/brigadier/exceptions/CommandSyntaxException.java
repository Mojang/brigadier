// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.Message;

public class CommandSyntaxException extends Exception {
    public static final int CONTEXT_AMOUNT = 10;
    public static boolean ENABLE_COMMAND_STACK_TRACES = true;
    public static BuiltInExceptionProvider BUILT_IN_EXCEPTIONS = new BuiltInExceptions();

    private final CommandExceptionType type;
    private final Message message;
    private final String input;
    private final int cursor;

    public CommandSyntaxException(final CommandExceptionType type, final Message message) {
        super(message.getString(), null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.message = message;
        this.input = null;
        this.cursor = -1;
    }

    public CommandSyntaxException(final CommandExceptionType type, final Message message, final String input, final int cursor) {
        super(message.getString(), null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.message = message;
        this.input = input;
        this.cursor = cursor;
    }

    @Override
    public String getMessage() {
        String message = this.message.getString();
        final String context = getContext();
        if (context != null) {
            message += " at position " + cursor + ": " + context;
        }
        return message;
    }

    public Message getRawMessage() {
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

    public String getInput() {
        return input;
    }

    public int getCursor() {
        return cursor;
    }
}
