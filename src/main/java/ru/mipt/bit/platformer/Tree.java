package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public final class Tree extends GameObject {
    private final GridPoint2 coordinates;

    public Tree(GridPoint2 coordinates) {
        this.coordinates = new GridPoint2(coordinates);
    }

    @Override
    public boolean occupies(GridPoint2 cell) {
        return coordinates.equals(cell);
    }

    @Override
    public Vector2 getPositionOnGrid() {
        return new Vector2(coordinates.x + 0.5f, coordinates.y + 0.5f);
    }
}
