package net.minecraft.commands.arguments;

import com.google.common.base.Splitter;
import net.minecraft.commands.exceptions.IllegalCommandArgumentException;

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

    @Override
    public CommandArgumentParseResult<Integer> parse(String command) throws IllegalCommandArgumentException {
        String raw = SPLITTER.split(command).iterator().next();

        try {
            int value = Integer.parseInt(raw);

            if (value < minimum) {
                throw new IllegalCommandArgumentException();
            }
            if (value > maximum) {
                throw new IllegalCommandArgumentException();
            }

            return new CommandArgumentParseResult<Integer>(raw, value);
        } catch (NumberFormatException ignored) {
            throw new IllegalCommandArgumentException();
        }
    }
}
