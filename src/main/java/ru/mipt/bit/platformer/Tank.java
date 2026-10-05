package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public final class Tank extends GameObject implements Movable {

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private final MovementSpace movementSpace;
    private final float movementDuration;

    private Direction direction;
    private float movementProgress = 1f;

    public Tank(
            GridPoint2 initialCoordinates,
            Direction initialDirection,
            MovementSpace movementSpace,
            float movementDuration
    ) {

        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be finite and positive");
        }
        this.coordinates = new GridPoint2(initialCoordinates);
        this.destinationCoordinates = new GridPoint2(initialCoordinates);
        this.direction = Objects.requireNonNull(initialDirection);
        this.movementSpace = Objects.requireNonNull(movementSpace);
        this.movementDuration = movementDuration;

    }

    /** Starts a step if the next cell is free; ignores commands during a step. */
    @Override
    public void move(Direction newDirection) {
        if (isMoving()) {
            return;
        }

        direction = Objects.requireNonNull(newDirection);

        GridPoint2 adjacentCoordinates =
                newDirection.getAdjacentCoordinates(coordinates);

        if (movementSpace.isFree(adjacentCoordinates, this)) {
            destinationCoordinates.set(adjacentCoordinates);
            movementProgress = 0f;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and non-negative");
        }
        if (!isMoving()) {
            return;
        }

        movementProgress = Math.min(1f, movementProgress + deltaTime / movementDuration);

        if (!isMoving()) {
            coordinates.set(destinationCoordinates);
        }
    }

    @Override
    public Vector2 getPositionOnGrid() {
        Vector2 start = new Vector2(coordinates.x + 0.5f, coordinates.y + 0.5f);
        Vector2 destination = new Vector2(
                destinationCoordinates.x + 0.5f, destinationCoordinates.y + 0.5f
        );
        return start.interpolate(destination, movementProgress, Interpolation.smooth);
    }

    @Override
    public boolean occupies(GridPoint2 cellCoordinates) {
        // Reserve both cells until the movement is complete.
        return coordinates.equals(cellCoordinates)
                || (isMoving() && destinationCoordinates.equals(cellCoordinates));
    }

    @Override
    public float getRotationDegrees() {
        return direction.getRotationDegrees();
    }

    private boolean isMoving() {
        return movementProgress < 1f;
    }

}
