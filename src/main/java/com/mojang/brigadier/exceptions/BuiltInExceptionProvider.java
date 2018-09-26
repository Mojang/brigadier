// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package com.mojang.brigadier.exceptions;

public interface BuiltInExceptionProvider {
    Dynamic2CommandExceptionType doubleTooLow();

    Dynamic2CommandExceptionType doubleTooHigh();

    Dynamic2CommandExceptionType floatTooLow();

    Dynamic2CommandExceptionType floatTooHigh();

    Dynamic2CommandExceptionType integerTooLow();

    Dynamic2CommandExceptionType integerTooHigh();

    Dynamic2CommandExceptionType longTooLow();

    Dynamic2CommandExceptionType longTooHigh();

    DynamicCommandExceptionType literalIncorrect();

    SimpleCommandExceptionType readerExpectedStartOfQuote();

    SimpleCommandExceptionType readerExpectedEndOfQuote();

    DynamicCommandExceptionType readerInvalidEscape();

    DynamicCommandExceptionType readerInvalidBool();

    DynamicCommandExceptionType readerInvalidInt();

    SimpleCommandExceptionType readerExpectedInt();

    DynamicCommandExceptionType readerInvalidLong();

    SimpleCommandExceptionType readerExpectedLong();

    DynamicCommandExceptionType readerInvalidDouble();

    SimpleCommandExceptionType readerExpectedDouble();

    DynamicCommandExceptionType readerInvalidFloat();

    SimpleCommandExceptionType readerExpectedFloat();

    SimpleCommandExceptionType readerExpectedBool();

    DynamicCommandExceptionType readerExpectedSymbol();

    SimpleCommandExceptionType dispatcherUnknownCommand();

    SimpleCommandExceptionType dispatcherUnknownArgument();

    SimpleCommandExceptionType dispatcherExpectedArgumentSeparator();

    DynamicCommandExceptionType dispatcherParseException();
}
