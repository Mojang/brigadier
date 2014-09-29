package net.minecraft.commands.context;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

public class ParsedArgumentTest {
    @Test
    public void testEquals() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new ParsedArgument<String>("foo", "bar"), new ParsedArgument<String>("foo", "bar"))
            .addEqualityGroup(new ParsedArgument<String>("bar", "baz"), new ParsedArgument<String>("bar", "baz"))
            .addEqualityGroup(new ParsedArgument<String>("foo", "baz"), new ParsedArgument<String>("foo", "baz"))
            .testEquals();
    }
}