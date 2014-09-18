package net.minecraft.commands.arguments;

import net.minecraft.commands.exceptions.IllegalCommandArgumentException;

public class IntegerArgumentType implements CommandArgumentType<Integer> {
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
    public Integer parse(String command) throws IllegalCommandArgumentException {
        try {
            int value = Integer.parseInt(command);

            if (value < minimum) {
                throw new IllegalCommandArgumentException();
            }
            if (value > maximum) {
                throw new IllegalCommandArgumentException();
            }

            return value;
        } catch (NumberFormatException ignored) {
            throw new IllegalCommandArgumentException();
        }
    }
}
