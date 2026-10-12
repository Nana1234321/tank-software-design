package ru.mipt.bit.platformer;

/** Supplies a complete level independently of rendering and input. */
@FunctionalInterface
public interface LevelSource {
    Level load(float movementDuration);
}
