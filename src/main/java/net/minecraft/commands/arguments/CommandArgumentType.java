package net.minecraft.commands.arguments;

import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public interface CommandArgumentType<T> {
    CommandArgumentParseResult<T> parse(String command) throws IllegalArgumentSyntaxException, ArgumentValidationException;

    class CommandArgumentParseResult<T> {
        private final String raw;
        private final T result;

        public CommandArgumentParseResult(String raw, T result) {
            this.raw = raw;
            this.result = result;
        }

        public String getRaw() {
            return raw;
        }

        public T getResult() {
            return result;
        }
    }
}
