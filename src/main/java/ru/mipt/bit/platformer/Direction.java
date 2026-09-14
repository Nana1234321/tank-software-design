package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

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
