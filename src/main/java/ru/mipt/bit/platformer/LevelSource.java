package ru.mipt.bit.platformer;

@FunctionalInterface
public interface LevelSource {
    Level load(float movementDuration);
}
