package com.mojang.brigadier;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandDispatcher<S> {
    public static final String ARGUMENT_SEPARATOR = " ";
    public static final char ARGUMENT_SEPARATOR_CHAR = ' ';
    private static final String USAGE_OPTIONAL_OPEN = "[";
    private static final String USAGE_OPTIONAL_CLOSE = "]";
    private static final String USAGE_REQUIRED_OPEN = "(";
    private static final String USAGE_REQUIRED_CLOSE = ")";
    private static final String USAGE_OR = "|";

    private final RootCommandNode<S> root;
    private final Predicate<CommandNode<S>> hasCommand = new Predicate<CommandNode<S>>() {
        @Override
        public boolean test(final CommandNode<S> input) {
            return input != null && (input.getCommand() != null || input.getChildren().stream().anyMatch(hasCommand));
        }
    };
    private ResultConsumer<S> consumer = (c, s, r) -> {
    };

    public CommandDispatcher(final RootCommandNode<S> root) {
        this.root = root;
    }

    public CommandDispatcher() {
        this(new RootCommandNode<>());
    }

    public LiteralCommandNode<S> register(final LiteralArgumentBuilder<S> command) {
        final LiteralCommandNode<S> build = command.build();
        root.addChild(build);
        return build;
    }

    public void setConsumer(final ResultConsumer<S> consumer) {
        this.consumer = consumer;
    }

    public int execute(final String input, final S source) throws CommandSyntaxException {
        return execute(new StringReader(input), source);
    }

    public int execute(final StringReader input, final S source) throws CommandSyntaxException {
        final ParseResults<S> parse = parse(input, source);
        return execute(parse);
    }

    public int execute(final ParseResults<S> parse) throws CommandSyntaxException {
        if (parse.getReader().canRead()) {
            if (parse.getExceptions().size() == 1) {
                throw parse.getExceptions().values().iterator().next();
            } else if (parse.getContext().getRange().isEmpty()) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
            } else {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
            }
        }

        int result = 0;
        int successfulForks = 0;
        boolean forked = false;
        boolean foundCommand = false;
        final String command = parse.getReader().getString();
        final CommandContext<S> original = parse.getContext().build(command);
        List<CommandContext<S>> contexts = Collections.singletonList(original);
        ArrayList<CommandContext<S>> next = null;

        while (contexts != null) {
            final int size = contexts.size();
            for (int i = 0; i < size; i++) {
                final CommandContext<S> context = contexts.get(i);
                final CommandContext<S> child = context.getChild();
                if (child != null) {
                    forked |= context.isForked();
                    if (!child.getNodes().isEmpty()) {
                        foundCommand = true;
                        final RedirectModifier<S> modifier = context.getRedirectModifier();
                        if (modifier == null) {
                            if (next == null) {
                                next = new ArrayList<>(1);
                            }
                            next.add(child.copyFor(context.getSource()));
                        } else {
                            try {
                                final Collection<S> results = modifier.apply(context);
                                if (!results.isEmpty()) {
                                    if (next == null) {
                                        next = new ArrayList<>(results.size());
                                    }
                                    for (final S source : results) {
                                        next.add(child.copyFor(source));
                                    }
                                }
                            } catch (final CommandSyntaxException ex) {
                                consumer.onCommandComplete(context, false, 0);
                                if (!forked) {
                                    throw ex;
                                }
                            }
                        }
                    }
                } else if (context.getCommand() != null) {
                    foundCommand = true;
                    try {
                        final int value = context.getCommand().run(context);
                        result += value;
                        consumer.onCommandComplete(context, true, value);
                        successfulForks++;
                    } catch (final CommandSyntaxException ex) {
                        consumer.onCommandComplete(context, false, 0);
                        if (!forked) {
                            throw ex;
                        }
                    }
                }
            }

            contexts = next;
            next = null;
        }

        if (!foundCommand) {
            consumer.onCommandComplete(original, false, 0);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
        }

        return forked ? successfulForks : result;
    }

    public ParseResults<S> parse(final String command, final S source) {
        return parse(new StringReader(command), source);
    }

    public ParseResults<S> parse(final StringReader command, final S source) {
        final CommandContextBuilder<S> context = new CommandContextBuilder<>(this, source, 0);
        return parseNodes(root, command, context);
    }

    private static class PartialParse<S> {
        public final CommandContextBuilder<S> context;
        public final ParseResults<S> parse;

        private PartialParse(final CommandContextBuilder<S> context, final ParseResults<S> parse) {
            this.context = context;
            this.parse = parse;
        }
    }

    private ParseResults<S> parseNodes(final CommandNode<S> node, final StringReader originalReader, final CommandContextBuilder<S> contextSoFar) {
        final S source = contextSoFar.getSource();
        Map<CommandNode<S>, CommandSyntaxException> errors = null;
        List<PartialParse<S>> potentials = null;
        final int cursor = originalReader.getCursor();

        for (final CommandNode<S> child : node.getRelevantNodes(originalReader)) {
            if (!child.canUse(source)) {
                continue;
            }
            final CommandContextBuilder<S> context = contextSoFar.copy();
            final StringReader reader = new StringReader(originalReader);
            try {
                try {
                    child.parse(reader, context);
                } catch (final RuntimeException ex) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, ex.getMessage());
                }
                if (reader.canRead()) {
                    if (reader.peek() != ARGUMENT_SEPARATOR_CHAR) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator().createWithContext(reader);
                    }
                }
            } catch (final CommandSyntaxException ex) {
                if (errors == null) {
                    errors = new LinkedHashMap<>();
                }
                errors.put(child, ex);
                reader.setCursor(cursor);
                continue;
            }

            context.withCommand(child.getCommand());
            if (reader.canRead(child.getRedirect() == null ? 2 : 1)) {
                reader.skip();
                if (child.getRedirect() != null) {
                    final CommandContextBuilder<S> childContext = new CommandContextBuilder<>(this, source, reader.getCursor());
                    childContext.withNode(child.getRedirect(), StringRange.between(cursor, reader.getCursor() - 1));
                    final ParseResults<S> parse = parseNodes(child.getRedirect(), reader, childContext);
                    context.withChild(parse.getContext());
                    return new ParseResults<>(context, originalReader.getCursor(), parse.getReader(), parse.getExceptions());
                } else {
                    final ParseResults<S> parse = parseNodes(child, reader, context);
                    if (potentials == null) {
                        potentials = new ArrayList<>(1);
                    }
                    potentials.add(new PartialParse<>(context, parse));
                }
            } else {
                if (potentials == null) {
                    potentials = new ArrayList<>(1);
                }
                potentials.add(new PartialParse<>(context, new ParseResults<>(context, originalReader.getCursor(), reader, Collections.emptyMap())));
            }
        }

        if (potentials != null) {
            if (potentials.size() > 1) {
                potentials.sort((a, b) -> {
                    if (!a.parse.getReader().canRead() && b.parse.getReader().canRead()) {
                        return -1;
                    }
                    if (a.parse.getReader().canRead() && !b.parse.getReader().canRead()) {
                        return 1;
                    }
                    if (a.parse.getExceptions().isEmpty() && !b.parse.getExceptions().isEmpty()) {
                        return -1;
                    }
                    if (!a.parse.getExceptions().isEmpty() && b.parse.getExceptions().isEmpty()) {
                        return 1;
                    }
                    return 0;
                });
            }
            return potentials.get(0).parse;
        }

        return new ParseResults<>(contextSoFar, originalReader.getCursor(), originalReader, errors == null ? Collections.emptyMap() : errors);
    }

    public String[] getAllUsage(final CommandNode<S> node, final S source, final boolean restricted) {
        final ArrayList<String> result = Lists.newArrayList();
        getAllUsage(node, source, result, "", restricted);
        return result.toArray(new String[result.size()]);
    }

    private void getAllUsage(final CommandNode<S> node, final S source, final ArrayList<String> result, final String prefix, final boolean restricted) {
        if (restricted && !node.canUse(source)) {
            return;
        }

        if (node.getCommand() != null) {
            result.add(prefix);
        }

        if (node.getRedirect() != null) {
            final String redirect = node.getRedirect() == root ? "..." : "-> " + node.getRedirect().getUsageText();
            result.add(prefix.isEmpty() ? node.getUsageText() + ARGUMENT_SEPARATOR + redirect : prefix + ARGUMENT_SEPARATOR + redirect);
        } else if (!node.getChildren().isEmpty()) {
            for (final CommandNode<S> child : node.getChildren()) {
                getAllUsage(child, source, result, prefix.isEmpty() ? child.getUsageText() : prefix + ARGUMENT_SEPARATOR + child.getUsageText(), restricted);
            }
        }
    }

    public Map<CommandNode<S>, String> getSmartUsage(final CommandNode<S> node, final S source) {
        final Map<CommandNode<S>, String> result = Maps.newLinkedHashMap();

        final boolean optional = node.getCommand() != null;
        for (final CommandNode<S> child : node.getChildren()) {
            final String usage = getSmartUsage(child, source, optional, false);
            if (usage != null) {
                result.put(child, usage);
            }
        }
        return result;
    }

    private String getSmartUsage(final CommandNode<S> node, final S source, final boolean optional, final boolean deep) {
        if (!node.canUse(source)) {
            return null;
        }

        final String self = optional ? USAGE_OPTIONAL_OPEN + node.getUsageText() + USAGE_OPTIONAL_CLOSE : node.getUsageText();
        final boolean childOptional = node.getCommand() != null;
        final String open = childOptional ? USAGE_OPTIONAL_OPEN : USAGE_REQUIRED_OPEN;
        final String close = childOptional ? USAGE_OPTIONAL_CLOSE : USAGE_REQUIRED_CLOSE;

        if (!deep) {
            if (node.getRedirect() != null) {
                final String redirect = node.getRedirect() == root ? "..." : "-> " + node.getRedirect().getUsageText();
                return self + ARGUMENT_SEPARATOR + redirect;
            } else {
                final Collection<CommandNode<S>> children = node.getChildren().stream().filter(c -> c.canUse(source)).collect(Collectors.toList());
                if (children.size() == 1) {
                    final String usage = getSmartUsage(children.iterator().next(), source, childOptional, childOptional);
                    if (usage != null) {
                        return self + ARGUMENT_SEPARATOR + usage;
                    }
                } else if (children.size() > 1) {
                    final Set<String> childUsage = Sets.newLinkedHashSet();
                    for (final CommandNode<S> child : children) {
                        final String usage = getSmartUsage(child, source, childOptional, true);
                        if (usage != null) {
                            childUsage.add(usage);
                        }
                    }
                    if (childUsage.size() == 1) {
                        final String usage = childUsage.iterator().next();
                        return self + ARGUMENT_SEPARATOR + (childOptional ? USAGE_OPTIONAL_OPEN + usage + USAGE_OPTIONAL_CLOSE : usage);
                    } else if (childUsage.size() > 1) {
                        final StringBuilder builder = new StringBuilder(open);
                        int count = 0;
                        for (final CommandNode<S> child : children) {
                            if (count > 0) {
                                builder.append(USAGE_OR);
                            }
                            builder.append(child.getUsageText());
                            count++;
                        }
                        if (count > 0) {
                            builder.append(close);
                            return self + ARGUMENT_SEPARATOR + builder.toString();
                        }
                    }
                }
            }
        }

        return self;
    }

    public CompletableFuture<Suggestions> getCompletionSuggestions(final ParseResults<S> parse) {
        final CommandContextBuilder<S> rootContext = parse.getContext();
        final CommandContextBuilder<S> context = rootContext.getLastChild();
        final CommandNode<S> parent;
        final int start;

        if (context.getNodes().isEmpty()) {
            parent = root;
            start = parse.getStartIndex();
        } else if (parse.getReader().canRead()) {
            final Map.Entry<CommandNode<S>, StringRange> entry = Iterables.getLast(context.getNodes().entrySet());
            parent = entry.getKey();
            start = entry.getValue().getEnd() + 1;
        } else if (context.getNodes().size() > 1) {
            final Map.Entry<CommandNode<S>, StringRange> entry = Iterables.get(context.getNodes().entrySet(), context.getNodes().size() - 2);
            parent = entry.getKey();
            start = entry.getValue().getEnd() + 1;
        } else if (rootContext != context && context.getNodes().size() > 0) {
            final Map.Entry<CommandNode<S>, StringRange> entry = Iterables.getLast(context.getNodes().entrySet());
            parent = entry.getKey();
            start = entry.getValue().getEnd() + 1;
        } else {
            parent = root;
            start = parse.getStartIndex();
        }

        @SuppressWarnings("unchecked") final CompletableFuture<Suggestions>[] futures = new CompletableFuture[parent.getChildren().size()];
        int i = 0;
        for (final CommandNode<S> node : parent.getChildren()) {
            CompletableFuture<Suggestions> future = Suggestions.empty();
            try {
                future = node.listSuggestions(context.build(parse.getReader().getString()), new SuggestionsBuilder(parse.getReader().getString(), start));
            } catch (final CommandSyntaxException ignored) {
            }
            futures[i++] = future;
        }

        final CompletableFuture<Suggestions> result = new CompletableFuture<>();
        CompletableFuture.allOf(futures).thenRun(() -> {
            final List<Suggestions> suggestions = Lists.newArrayList();
            for (final CompletableFuture<Suggestions> future : futures) {
                suggestions.add(future.join());
            }
            result.complete(Suggestions.merge(parse.getReader().getString(), suggestions));
        });

        return result;
    }

    public RootCommandNode<S> getRoot() {
        return root;
    }

    public Collection<String> getPath(final CommandNode<S> target) {
        final List<List<CommandNode<S>>> nodes = new ArrayList<>();
        addPaths(root, nodes, new ArrayList<>());

        for (final List<CommandNode<S>> list : nodes) {
            if (list.get(list.size() - 1) == target) {
                final List<String> result = new ArrayList<>(list.size());
                for (final CommandNode<S> node : list) {
                    if (node != root) {
                        result.add(node.getName());
                    }
                }
                return result;
            }
        }

        return Collections.emptyList();
    }

    public CommandNode<S> findNode(final Collection<String> path) {
        CommandNode<S> node = root;
        for (final String name : path) {
            node = node.getChild(name);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    public void findAmbiguities(final AmbiguityConsumer<S> consumer) {
        root.findAmbiguities(consumer);
    }

    private void addPaths(final CommandNode<S> node, final List<List<CommandNode<S>>> result, final List<CommandNode<S>> parents) {
        final List<CommandNode<S>> current = new ArrayList<>(parents);
        current.add(node);
        result.add(current);

        for (final CommandNode<S> child : node.getChildren()) {
            addPaths(child, result, current);
        }
    }
}
