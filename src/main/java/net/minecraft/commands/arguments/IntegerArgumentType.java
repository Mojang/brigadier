package net.minecraft.commands.arguments;

import com.google.common.base.Splitter;
import net.minecraft.commands.context.CommandContext;
import net.minecraft.commands.exceptions.ArgumentValidationException;
import net.minecraft.commands.exceptions.IllegalArgumentSyntaxException;

public class IntegerArgumentType implements CommandArgumentType<Integer> {
    private static final Splitter SPLITTER = Splitter.on(' ').limit(2);

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

    public static int getInteger(CommandContext context, String name) {
        return context.getArgument(name, int.class).getResult();
    }

    @Override
    public CommandArgumentParseResult<Integer> parse(String command) throws IllegalArgumentSyntaxException, ArgumentValidationException {
        String raw = SPLITTER.split(command).iterator().next();

        try {
            int value = Integer.parseInt(raw);

            if (value < minimum) {
                throw new ArgumentValidationException();
            }
            if (value > maximum) {
                throw new ArgumentValidationException();
            }

            return new CommandArgumentParseResult<Integer>(raw, value);
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentSyntaxException();
        }
    }
}
