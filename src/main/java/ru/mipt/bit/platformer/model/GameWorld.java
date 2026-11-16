package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.List;

/**
 * Операции с игровым миром, которые нужны танкам и снарядам.
 * Это внутренний порт domain, его реализует Field.
 * Он не обращается к внешним сервисам, но пока использует GridPoint2 из LibGDX.
 */
public interface GameWorld extends MovementSpace {
    void addObject(GameObject object);

    void removeObject(GameObject object);

    boolean isInside(GridPoint2 cell);

    List<GameObject> getObjectsAt(GridPoint2 cell);
}
