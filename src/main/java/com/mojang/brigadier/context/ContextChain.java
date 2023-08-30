package com.mojang.brigadier.context;

import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ContextChain<S> {
    // TODO ideally those two would have separate types, but modifiers and executables expect full context
    private final List<CommandContext<S>> modifiers;
    private final CommandContext<S> executable;

    private ContextChain<S> nextStageCache = null;

    public ContextChain(final List<CommandContext<S>> modifiers, final CommandContext<S> executable) {
        if (executable.getCommand() == null) {
            throw new IllegalArgumentException("Last command in chain must be executable");
        }
        this.modifiers = modifiers;
        this.executable = executable;
    }

    public static <S> Optional<ContextChain<S>> tryFlatten(final CommandContext<S> rootContext) {
        final List<CommandContext<S>> modifiers = new ArrayList<>();

        CommandContext<S> current = rootContext;

        while (true) {
            final CommandContext<S> child = current.getChild();
            if (child == null) {
                // Last entry must be executable command
                if (current.getCommand() == null) {
                    return Optional.empty();
                }

                return Optional.of(new ContextChain<>(modifiers, current));
            }

            modifiers.add(current);
            current = child;
        }
    }

    public static <S> Collection<S> runModifier(final CommandContext<S> modifier, final S source, final ResultConsumer<S> resultConsumer, final boolean forkedMode) throws CommandSyntaxException {
        final RedirectModifier<S> sourceModifier = modifier.getRedirectModifier();

        // Note: source currently in context is irrelevant at this point, since we might have updated it in one of earlier stages
        if (sourceModifier == null) {
            // Simple redirect, just propagate source to next node
            return Collections.singleton(source);
        }

        final CommandContext<S> contextToUse = modifier.copyFor(source);
        try {
            return sourceModifier.apply(contextToUse);
        } catch (final CommandSyntaxException ex) {
            resultConsumer.onCommandComplete(contextToUse, false, 0);
            if (forkedMode) {
                return Collections.emptyList();
            }
            throw ex;
        }
    }

    public static <S> int runExecutable(final CommandContext<S> executable, final S source, final ResultConsumer<S> resultConsumer, final boolean forkedMode) throws CommandSyntaxException {
        final CommandContext<S> contextToUse = executable.copyFor(source);
        try {
            final int result = executable.getCommand().run(contextToUse);
            resultConsumer.onCommandComplete(contextToUse, true, result);
            return forkedMode ? 1 : result;
        } catch (final CommandSyntaxException ex) {
            resultConsumer.onCommandComplete(contextToUse, false, 0);
            if (forkedMode) {
                return 0;
            }
            throw ex;
        }
    }

    public int executeAll(final S source, final ResultConsumer<S> resultConsumer) throws CommandSyntaxException {
        if (modifiers.isEmpty()) {
            // Fast path - just a single stage
            return runExecutable(executable, source, resultConsumer, false);
        }

        boolean forkedMode = false;
        List<S> currentSources = Collections.singletonList(source);

        for (final CommandContext<S> modifier : modifiers) {
            forkedMode |= modifier.isForked();

            List<S> nextSources = new ArrayList<>();
            for (final S sourceToRun : currentSources) {
                nextSources.addAll(runModifier(modifier, sourceToRun, resultConsumer, forkedMode));
            }
            if (nextSources.isEmpty()) {
                return 0;
            }
            currentSources = nextSources;
        }

        int result = 0;
        for (final S executionSource : currentSources) {
            result += runExecutable(executable, executionSource, resultConsumer, forkedMode);
        }

        return result;
    }

    public Stage getStage() {
        return modifiers.isEmpty() ? Stage.EXECUTE : Stage.MODIFY;
    }

    public CommandContext<S> getTopContext() {
        if (modifiers.isEmpty()) {
            return executable;
        }
        return modifiers.get(0);
    }

    public ContextChain<S> nextStage() {
        final int modifierCount = modifiers.size();
        if (modifierCount == 0) {
            return null;
        }

        if (nextStageCache == null) {
            nextStageCache = new ContextChain<>(modifiers.subList(1, modifierCount), executable);
        }
        return nextStageCache;
    }

    public enum Stage {
        MODIFY,
        EXECUTE,
    }
}
