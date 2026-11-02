package ru.mipt.bit.platformer.model;

@FunctionalInterface
public interface Movable {
    void move(Direction direction);
}
