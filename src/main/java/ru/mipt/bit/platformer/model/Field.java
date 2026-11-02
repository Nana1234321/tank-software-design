package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Field implements GameWorld {
    private final int width;
    private final int height;
    private final List<GameObject> objects = new ArrayList<>();
    private final List<FieldListener> listeners = new ArrayList<>();

    public Field(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }

    /** Subscribe once and deliver existing objects through the same addition event. */
    public void addListener(FieldListener listener) {
        Objects.requireNonNull(listener);
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
        for (GameObject object : new ArrayList<>(objects)) {
            if (!listeners.contains(listener)) {
                break;
            }
            if (objects.contains(object)) {
                listener.onObjectAdded(object);
            }
        }
    }

    public void removeListener(FieldListener listener) {
        listeners.remove(Objects.requireNonNull(listener));
    }

    @Override
    public void addObject(GameObject object) {
        validateRegistration(object);
        objects.add(object);
        notifyObjectAdded(object);
    }

    @Override
    public void removeObject(GameObject object) {
        if (objects.remove(Objects.requireNonNull(object))) {
            notifyObjectRemoved(object);
        }
    }

    private void validateRegistration(GameObject object) {
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
    }

    private void notifyObjectAdded(GameObject object) {
        for (FieldListener listener : new ArrayList<>(listeners)) {
            if (listeners.contains(listener) && objects.contains(object)) {
                listener.onObjectAdded(object);
            }
        }
    }

    private void notifyObjectRemoved(GameObject object) {
        for (FieldListener listener : new ArrayList<>(listeners)) {
            if (listeners.contains(listener) && !objects.contains(object)) {
                listener.onObjectRemoved(object);
            }
        }
    }

    @Override
    public List<GameObject> getObjectsAt(GridPoint2 cell) {
        List<GameObject> occupants = new ArrayList<>();
        for (GameObject object : objects) {
            if (object.occupies(cell)) {
                occupants.add(object);
            }
        }
        return occupants;
    }

    @Override
    public boolean isInside(GridPoint2 cell) {
        return cell.x >= 0 && cell.y >= 0 && cell.x < width && cell.y < height;
    }

    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public boolean isFree(GridPoint2 cell, GameObject asker) {
        if (!isInside(cell)) {
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
        for (GameObject object : new ArrayList<>(objects)) {
            if (objects.contains(object)) {
                object.update(deltaTime);
            }
        }
    }

    private void requireFreeCell(GridPoint2 cell) {
        if (!isFree(cell, null)) {
            throw new IllegalArgumentException("Cannot spawn an object at " + cell);
        }
    }
}
