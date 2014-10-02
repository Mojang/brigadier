package net.minecraft.commands.exceptions;

public interface CommandExceptionType {
    String getTypeName();
    String getErrorMessage(CommandException exception);
}
