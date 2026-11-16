package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Направление движения в игровой модели (domain).
 * Это значение, а не отдельная сущность. Метод соседней клетки пока возвращает
 * GridPoint2 из LibGDX; при отделении модели его тоже нужно заменить.
 */
public enum Direction {

    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final int deltaX;
    private final int deltaY;
    private final float rotationDegrees;

    Direction(int deltaX, int deltaY, float rotationDegrees) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.rotationDegrees = rotationDegrees;
    }

    public GridPoint2 getAdjacentCoordinates(GridPoint2 origin) {
        return new GridPoint2(
                origin.x + deltaX,
                origin.y + deltaY
        );
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }
}
