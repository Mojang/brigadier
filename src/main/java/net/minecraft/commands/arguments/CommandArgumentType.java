package net.minecraft.commands.arguments;

import net.minecraft.commands.exceptions.IllegalCommandArgumentException;

public interface CommandArgumentType<T> {
    T parse(String command) throws IllegalCommandArgumentException;
}
