// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier;

/**
 * An abstract notion of a message that can be displayed to a user.
 */
public interface Message {

    /**
     * Returns the content of the message.
     *
     * @return the content of the message
     */
    String getString();
}
