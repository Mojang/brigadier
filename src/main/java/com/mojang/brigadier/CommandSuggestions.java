package com.mojang.brigadier;

import com.mojang.brigadier.context.StringRange;

import java.util.List;
import java.util.Objects;

public class CommandSuggestions {
    private final StringRange range;
    private final List<String> suggestions;

    public CommandSuggestions(final StringRange range, final List<String> suggestions) {
        this.range = range;
        this.suggestions = suggestions;
    }

    public StringRange getRange() {
        return range;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandSuggestions)) {
            return false;
        }
        final CommandSuggestions that = (CommandSuggestions) o;
        return Objects.equals(range, that.range) && Objects.equals(suggestions, that.suggestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, suggestions);
    }

    public boolean isEmpty() {
        return suggestions.isEmpty();
    }
}
