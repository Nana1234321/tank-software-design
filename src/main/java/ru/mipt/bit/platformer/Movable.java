package ru.mipt.bit.platformer;

@FunctionalInterface
public interface Movable {
    void move(Direction direction);
}
