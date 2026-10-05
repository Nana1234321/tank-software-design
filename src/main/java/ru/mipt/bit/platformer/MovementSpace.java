package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

/** Provides the occupancy check needed to start a movement step. */
@FunctionalInterface
public interface MovementSpace {
    boolean isFree(GridPoint2 cell, GameObject asker);
}
