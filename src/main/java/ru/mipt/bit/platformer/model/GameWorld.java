package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.List;

/** Logical world operations used by tanks and projectiles. */
public interface GameWorld extends MovementSpace {
    void addObject(GameObject object);

    void removeObject(GameObject object);

    boolean isInside(GridPoint2 cell);

    List<GameObject> getObjectsAt(GridPoint2 cell);
}
