package net.minecraft.commands.arguments;

import com.google.common.base.Splitter;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.context.CommandContext;
import net.minecraft.commands.context.ParsedArgument;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public class IntegerArgumentType implements CommandArgumentType<Integer> {
    private static final Splitter SPLITTER = Splitter.on(CommandDispatcher.ARGUMENT_SEPARATOR).limit(2);

    private final int minimum;
    private final int maximum;

    protected IntegerArgumentType(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static IntegerArgumentType integer() {
        return integer(Integer.MIN_VALUE);
    }

    public static IntegerArgumentType integer(int min) {
        return integer(min, Integer.MAX_VALUE);
    }

    public static IntegerArgumentType integer(int min, int max) {
        return new IntegerArgumentType(min, max);
    }

    public static int getInteger(CommandContext<?> context, String name) {
        return context.getArgument(name, int.class).getResult();
    }

    @Override
    public ParsedArgument<Integer> parse(String command) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        String raw = SPLITTER.split(command).iterator().next();

        try {
            int value = Integer.parseInt(raw);

            if (value < minimum) {
                throw new ArgumentValidationException();
            }
            if (value > maximum) {
                throw new ArgumentValidationException();
            }

            return new ParsedArgument<Integer>(raw, value);
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentSyntaxException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerArgumentType)) return false;

        IntegerArgumentType that = (IntegerArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return 31 * minimum + maximum;
    }

    @Override
    public String toString() {
        if (minimum == Integer.MIN_VALUE && maximum == Integer.MAX_VALUE) {
            return "integer()";
        } else if (maximum == Integer.MAX_VALUE) {
            return "integer(" + minimum + ")";
        } else {
            return "integer(" + minimum + ", " + maximum + ")";
        }
    }
}
