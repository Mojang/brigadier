// Copyright (c) Serena Lynas. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.results;

import java.util.ArrayList;
import java.util.List;

public class ListCommandResult implements CommandResult {
    private List<Object> results = new ArrayList<>();

    public List<Object> getResults() {
        return results;
    }

    @Override
    public ListCommandResult combine(Object other) {
        if (!(other instanceof EmptyCommandResult)) {
            results.add(other);
        }

        return this;
    }

    public static ListCommandResult from(Object a, Object b) {
        ListCommandResult list = new ListCommandResult();
        list = list.combine(a);
        list = list.combine(b);
        return list;
    }
}
