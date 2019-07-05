package com.mojang.brigadier.util;

import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * Helper class to work with {@link Predicate}
 */
public final class PredicateUtil {

    private PredicateUtil() { }

    /**
     * Combines given predicates into single one
     */
    public static <T> Predicate<T> join(Iterable<Predicate<T>> predicates) {
        return StreamSupport.stream(predicates.spliterator(), false).reduce(alwaysTrue(), Predicate::and);
    }

    private static <T> Predicate<T> alwaysTrue() {
        return t -> true;
    }
}
