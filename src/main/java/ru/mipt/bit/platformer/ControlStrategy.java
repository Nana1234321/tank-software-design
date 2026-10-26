package ru.mipt.bit.platformer;

@FunctionalInterface
public interface ControlStrategy {
    /** Returns null when no action is requested. */
    Command nextCommand(Movable target);
}
