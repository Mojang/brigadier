package net.minecraft.commands.context;

public class ParsedArgument<T> {
    private final String raw;
    private final T result;

    public ParsedArgument(String raw, T result) {
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
