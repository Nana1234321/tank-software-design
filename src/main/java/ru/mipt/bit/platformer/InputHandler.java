package ru.mipt.bit.platformer;

/** A frame-based input handler. New controls can implement this contract. */
@FunctionalInterface
public interface InputHandler {
    void handleInput();
}
