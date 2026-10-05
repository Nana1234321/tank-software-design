package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Field implements MovementSpace {
    private final int width;
    private final int height;
    private final List<GameObject> objects = new ArrayList<>();

    public Field(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }

    /** Registers an object after checking its position and occupied cells. */
    public <T extends GameObject> T addObject(T object) {
        Objects.requireNonNull(object);
        if (objects.contains(object)) {
            throw new IllegalArgumentException("Object is already registered");
        }
        Vector2 position = object.getPositionOnGrid();
        if (!Float.isFinite(position.x) || !Float.isFinite(position.y)
                || position.x < 0f || position.y < 0f
                || position.x >= width || position.y >= height) {
            throw new IllegalArgumentException("Object position is outside the field");
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                GridPoint2 cell = new GridPoint2(x, y);
                if (object.occupies(cell)) {
                    requireFreeCell(cell);
                }
            }
        }
        objects.add(object);
        return object;
    }

    /** A read-only view; registration remains the field's responsibility. */
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    @Override
    public boolean isFree(GridPoint2 cell, GameObject asker) {
        if (cell.x < 0 || cell.y < 0 || cell.x >= width || cell.y >= height) {
            return false;
        }
        for (GameObject object : objects) {
            if (object != asker && object.occupies(cell)) {
                return false;
            }
        }
        return true;
    }

    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and non-negative");
        }
        for (GameObject object : objects) {
            object.update(deltaTime);
        }
    }

    private void requireFreeCell(GridPoint2 cell) {
        if (!isFree(cell, null)) {
            throw new IllegalArgumentException("Cannot spawn an object at " + cell);
        }
    }
}
