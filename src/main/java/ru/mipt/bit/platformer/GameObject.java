package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    public abstract boolean occupies(GridPoint2 cell);

    /** Returns a new center position in grid units, never internal mutable state. */
    public abstract Vector2 getPositionOnGrid();

    public float getRotationDegrees() {
        return 0f;
    }

    public void update(float deltaTime) {
    }
}
