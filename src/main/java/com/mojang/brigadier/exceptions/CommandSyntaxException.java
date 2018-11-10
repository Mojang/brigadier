// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.Message;

/**
 * An exception for a syntax error that occurred while parsing or executing a command.
 * <p>
 * TODO: Why is this named syntax exception, when it can also occur when executing a command?
 */
public class CommandSyntaxException extends Exception {
    public static final int CONTEXT_AMOUNT = 10;
    public static boolean ENABLE_COMMAND_STACK_TRACES = true;
    public static BuiltInExceptionProvider BUILT_IN_EXCEPTIONS = new BuiltInExceptions();

    private final CommandExceptionType type;
    private final Message message;
    private final String input;
    private final int cursor;

    /**
     * Creates a new {@link CommandSyntaxException} of a given type and with a given message.
     *
     * @param type the type of the exception
     * @param message the message
     */
    public CommandSyntaxException(final CommandExceptionType type, final Message message) {
        super(message.getString(), null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.message = message;
        this.input = null;
        this.cursor = -1;
    }

    /**
     * Creates a new {@link CommandSyntaxException} of a given type and message together with the input and cursor
     * position.
     *
     * @param type the type of the exception
     * @param message the message
     * @param input the input that caused the exception
     * @param cursor the cursor position the exception occurred on
     */
    public CommandSyntaxException(final CommandExceptionType type, final Message message, final String input, final int cursor) {
        super(message.getString(), null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.type = type;
        this.message = message;
        this.input = input;
        this.cursor = cursor;
    }

    /**
     * Returns the message together with the position it occurred on and some context.
     *
     * @return the message together with the position it occurred on and some context.
     */
    @Override
    public String getMessage() {
        String message = this.message.getString();
        final String context = getContext();
        if (context != null) {
            message += " at position " + cursor + ": " + context;
        }
        return message;
    }

    /**
     * Returns the raw message, not including positional information
     *
     * @return the raw message without any formatting or positional information
     */
    public Message getRawMessage() {
        return message;
    }

    /**
     * Returns some contextual information about where the error occurred.
     * <p>
     * This is done by returning a few characters of the input and a pointer to where the exception it happened.
     *
     * @return some contextual information about where the error occurred or null if {@link #getInput()} or
     * {@link #getCursor()} are null/0.
     */
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

    /**
     * Returns the type of the exception.
     *
     * @return the type of the exception
     */
    public CommandExceptionType getType() {
        return type;
    }

    /**
     * Returns the input that caused the {@link CommandSyntaxException}.
     *
     * @return the input that caused the {@link CommandSyntaxException} or null if not set
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns the cursor position.
     *
     * @return the cursor position or -1 if not set
     */
    public int getCursor() {
        return cursor;
    }
}
