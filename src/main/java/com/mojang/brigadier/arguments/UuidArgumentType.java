package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.UUID;

public class UuidArgumentType implements ArgumentType<UUID> {

    private UuidArgumentType() { }

    public static UuidArgumentType uuid() {
        return new UuidArgumentType();
    }

    @Override
    public UUID parse(StringReader reader) throws CommandSyntaxException {
        return UUID.fromString(reader.readUnquotedString()
                .replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
