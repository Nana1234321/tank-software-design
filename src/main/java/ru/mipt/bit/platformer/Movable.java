package ru.mipt.bit.platformer;

/** Accepts directional movement commands. */
@FunctionalInterface
public interface Movable {
    void move(Direction direction);
}
