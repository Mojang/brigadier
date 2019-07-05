package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.Collection;

public class ByteArgumentType implements ArgumentType<Byte> {
    private static final Collection<String> EXAMPLES = Arrays.asList("-128", "0", "127");

    private final byte minimum;
    private final byte maximum;

    private ByteArgumentType(byte minimum, byte maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static ByteArgumentType byteArg() {
        return byteArg(Byte.MIN_VALUE);
    }

    public static ByteArgumentType byteArg(final byte minimum) {
        return byteArg(minimum, Byte.MAX_VALUE);
    }

    public static ByteArgumentType byteArg(final byte minimum, final byte maximum) {
        return new ByteArgumentType(minimum, maximum);
    }

    public static byte getByte(final CommandContext<?> context, final String name) {
        return context.getArgument(name, byte.class);
    }

    public byte getMinimum() {
        return minimum;
    }

    public byte getMaximum() {
        return maximum;
    }

    @Override
    public Byte parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final byte result = reader.readByte();
        if (result < minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.byteTooLow().createWithContext(reader, result, minimum);
        }
        if (result > maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.byteTooHigh().createWithContext(reader, result, maximum);
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteArgumentType)) return false;

        final ByteArgumentType that = (ByteArgumentType) o;
        return maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return 31 * minimum + maximum;
    }

    @Override
    public String toString() {
        if (minimum == Byte.MIN_VALUE && maximum == Byte.MAX_VALUE) {
            return "byteArg()";
        } else if (maximum == Byte.MAX_VALUE) {
            return "byteArg(" + minimum + ")";
        } else {
            return "byteArg(" + minimum + ", " + maximum + ")";
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
