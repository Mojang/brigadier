# Brigadier [![Latest release](https://img.shields.io/github/release/Mojang/brigadier.svg)](https://github.com/Mojang/brigadier/releases/latest) [![License](https://img.shields.io/github/license/Mojang/brigadier.svg)](https://github.com/Mojang/brigadier/blob/master/LICENSE)

Brigadier is a command parser & dispatcher, designed and developed for Minecraft: Java Edition and now freely available for use elsewhere under the MIT license.

# Installation
Brigadier is available to Maven & Gradle via `libraries.minecraft.net`. Its group is `com.mojang`, and artifact name is `brigadier`.

## Gradle
First include our repository:
```groovy
maven {
    url "https://libraries.minecraft.net"
}
```

And then use this library (change `(the latest version)` to the latest version!):
```groovy
compile 'com.mojang:brigadier:(the latest version)'
```

## Maven
First include our repository:
```xml
<repository>
  <id>minecraft-libraries</id>
  <name>Minecraft Libraries</name>
  <url>https://libraries.minecraft.net</url>
</repository>
```

And then use this library (change `(the latest version)` to the latest version!):
```xml
<dependency>
    <groupId>com.mojang</groupId>
    <artifactId>brigadier</artifactId>
    <version>(the latest version)</version>
</dependency>
```

# Contributing
Contributions are welcome! :D

Most contributions will require you to agree to a Contributor License Agreement (CLA) declaring that you have the right to,
and actually do, grant us the rights to use your contribution. For details, visit https://cla.microsoft.com.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

# Usage
At the heart of Brigadier, you need a `CommandDispatcher<S>`, where `<S>` is any custom object you choose to identify a "command source".

A command dispatcher holds a "command tree", which is a series of `CommandNode`s that represent the various possible syntax options that form a valid command.

## Registering a new command
Before we can start parsing and dispatching commands, we need to build up our command tree. Every registration is an append operation,
so you can freely extend existing commands in a project without needing access to the source code that created them.

Command registration also encourages the use of a builder pattern to keep code cruft to a minimum.

A "command" is a fairly loose term, but typically it means an exit point of the command tree.
Every node can have an `executes` function attached to it, which signifies that if the input stops here then this function will be called with the context so far.

Consider the following example:
```java
CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

dispatcher.register(
    literal("foo")
        .then(
            argument("bar", integer())
                .executes(c -> {
                    System.out.println("Bar is " + getInteger(c, "bar"));
                    return 1;
                })
        )
        .executes(c -> {
            System.out.println("Called foo with no arguments");
            return 1;
        })
);
``` 

This snippet registers two "commands": `foo` and `foo <bar>`. It is also common to refer to the `<bar>` as a "subcommand" of `foo`, as it's a child node.

At the start of the tree is a "root node", and it **must** have `LiteralCommandNode`s as children. Here, we register one command under the root: `literal("foo")`, which means "the user must type the literal string 'foo'".

Under that is two extra definitions: a child node for possible further evaluation, or an `executes` block if the user input stops here.

The child node works exactly the same way, but is no longer limited to literals. The other type of node that is now allowed is an `ArgumentCommandNode`, which takes in a name and an argument type.

Arguments can be anything, and you are encouraged to build your own for seamless integration into your own product. There are some standard arguments included in brigadier, such as `IntegerArgumentType`.

Argument types will be asked to parse input as much as they can, and then store the "result" of that argument however they see fit or throw a relevant error if they can't parse.

For example, an integer argument would parse "123" and store it as `123` (`int`), but throw an error if the input were `onetwothree`.

When a command is actually run, it can access these arguments in the context provided to the registered function.

## Parsing user input
So, we've registered some commands and now we're ready to take in user input. If you're in a rush, you can just call `dispatcher.execute("foo 123", source)` and call it a day.

The result of `execute` is an integer that was returned from an evaluated command. The meaning of this integer depends on the command, and will typically not be useful to programmers.

The `source` is an object of `<S>`, your own custom class to track users/players/etc. It will be provided to the command so that it has some context on what's happening.

If the command failed or could not parse, some form of `CommandSyntaxException` will be thrown. It is also possible for a `RuntimeException` to be bubbled up, if not properly handled in a command.

If you wish to have more control over the parsing & executing of commands, or wish to cache the parse results so you can execute it multiple times, you can split it up into two steps:

```java
final ParseResults<S> parse = dispatcher.parse("foo 123", source);
final int result = execute(parse);
``` 

This is highly recommended as the parse step is the most expensive, and may be easily cached depending on your application.

You can also use this to do further introspection on a command, before (or without) actually running it.

## Inspecting a command
If you `parse` some input, you can find out what it will perform (if anything) and provide hints to the user safely and immediately.

The parse will never fail, and the `ParseResults<S>` it returns will contain a *possible* context that a command may be called with
(and from that, you can inspect which nodes the user entered, complete with start/end positions in the input string).
It also contains a map of parse exceptions for each command node it encountered. If it couldn't build a valid context, then
the reason why is inside this exception map.

## Displaying usage info
There are two forms of "usage strings" provided by this library, both require a target node.

`getAllUsage(node, source, restricted)`  will return a list of all possible commands (executable end-points) under the target node and their human readable path. If `restricted`, it will ignore commands that `source` does not have access to. This will look like [`foo`, `foo <bar>`].

`getSmartUsage(node, source)` will return a map of the child nodes to their "smart usage" human readable path. This tries to squash future-nodes together and show optional & typed information, and can look like `foo (<bar>)`.

[![GitHub forks](https://img.shields.io/github/forks/Mojang/brigadier.svg?style=social&label=Fork)](https://github.com/Mojang/brigadier/fork) [![GitHub stars](https://img.shields.io/github/stars/Mojang/brigadier.svg?style=social&label=Stars)](https://github.com/Mojang/brigadier/stargazers)
