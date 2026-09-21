package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Field {
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

    public Tank spawnTank(GridPoint2 cell, Direction direction, float movementDuration) {
        requireFreeCell(cell);
        Tank tank = new Tank(cell, direction, this, movementDuration);
        objects.add(tank);
        return tank;
    }

    public Tree spawnTree(GridPoint2 cell) {
        requireFreeCell(cell);
        Tree tree = new Tree(cell);
        objects.add(tree);
        return tree;
    }

    /** A read-only view; registration remains the field's responsibility. */
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

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
