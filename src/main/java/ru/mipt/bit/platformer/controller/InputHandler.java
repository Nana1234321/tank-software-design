package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.command.Command;

@FunctionalInterface
public interface InputHandler {
    /** Creates a command without executing it; returns null when no action is requested. */
    Command createCommand();
}
