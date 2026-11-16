package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Позволяет узнать, свободна ли клетка.
 * Это узкий внутренний порт domain, реализованный в Field через GameWorld.
 * Тип клетки пока взят из LibGDX.
 */
@FunctionalInterface
public interface MovementSpace {
    boolean isFree(GridPoint2 cell, GameObject asker);
}
