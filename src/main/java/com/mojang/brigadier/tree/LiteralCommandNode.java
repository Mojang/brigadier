package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.ParameterizedCommandExceptionType;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class LiteralCommandNode<S> extends CommandNode<S> {
    public static final ParameterizedCommandExceptionType ERROR_INCORRECT_LITERAL = new ParameterizedCommandExceptionType("argument.literal.incorrect", "Expected literal ${expected}", "expected");

    private final String literal;

    public LiteralCommandNode(final String literal, final Command<S> command, final Predicate<S> requirement, final CommandNode<S> redirect, final Function<CommandContext<S>, Collection<S>> modifier) {
        super(command, requirement, redirect, modifier);
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    protected Object getMergeKey() {
        return literal;
    }

    @Override
    public void parse(final StringReader reader, final CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        for (int i = 0; i < literal.length(); i++) {
            if (reader.canRead() && reader.peek() == literal.charAt(i)) {
                reader.skip();
            } else {
                reader.setCursor(start);
                throw ERROR_INCORRECT_LITERAL.createWithContext(reader, literal);
            }
        }

        contextBuilder.withNode(this, literal);
    }

    @Override
    public void listSuggestions(final String command, final Set<String> output, final CommandContextBuilder<S> contextBuilder) {
        if (literal.startsWith(command)) {
            output.add(literal);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof LiteralCommandNode)) return false;

        final LiteralCommandNode that = (LiteralCommandNode) o;

        if (!literal.equals(that.literal)) return false;
        return super.equals(o);
    }

    @Override
    public String getUsageText() {
        return literal;
    }

    @Override
    public int hashCode() {
        int result = literal.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public LiteralArgumentBuilder<S> createBuilder() {
        final LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(this.literal);
        builder.requires(getRequirement());
        builder.redirect(getRedirect(), getRedirectModifier());
        if (getCommand() != null) {
            builder.executes(getCommand());
        }
        return builder;
    }

    @Override
    protected String getSortedKey() {
        return literal;
    }
}
