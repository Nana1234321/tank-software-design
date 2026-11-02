package ru.mipt.bit.platformer.model;

@FunctionalInterface
public interface LevelSource {
    Level load(float movementDuration);
}
