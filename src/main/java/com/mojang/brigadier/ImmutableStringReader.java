// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

import java.util.function.Predicate;

public interface ImmutableStringReader {
    String getString();

    int getRemainingLength();

    int getTotalLength();

    int getCursor();

    String getRead();

    String getRemaining();

    boolean canRead(int length);

    boolean canRead();

    char peek();

    char peek(int offset);

    boolean isNext(char c);

    boolean isNext(Predicate<Character> predicate);
}
