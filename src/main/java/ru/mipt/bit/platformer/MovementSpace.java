package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

@FunctionalInterface
public interface MovementSpace {
    boolean isFree(GridPoint2 cell, GameObject asker);
}
