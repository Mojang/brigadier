package com.mojang.brigadier.exceptions;

import java.util.Map;

public interface CommandExceptionType {
    String getTypeName();

    String getErrorMessage(Map<String, String> data);
}
