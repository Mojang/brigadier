package com.mojang.brigadier.exceptions;

public interface CommandExceptionType {
    String getTypeName();

    String getErrorMessage(CommandException exception);
}
