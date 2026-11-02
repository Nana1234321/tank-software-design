package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.Objects;

public final class Bullet extends GameObject {
    public static final int DEFAULT_DAMAGE = 25;

    private final GridPoint2 coordinates;
    private final Direction direction;
    private final GameWorld world;
    private final float movementDuration;
    private final int damage;
    private float movementProgress;
    private boolean destroyed;

    public Bullet(GridPoint2 initialCoordinates, Direction direction,
                  GameWorld world, float movementDuration) {
        this(initialCoordinates, direction, world, movementDuration, DEFAULT_DAMAGE);
    }

    public Bullet(GridPoint2 initialCoordinates, Direction direction,
                  GameWorld world, float movementDuration, int damage) {
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be finite and positive");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be non-negative");
        }
        this.coordinates = new GridPoint2(initialCoordinates);
        this.direction = Objects.requireNonNull(direction);
        this.world = Objects.requireNonNull(world);
        this.movementDuration = movementDuration;
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    /** Resolve a point-blank hit before attempting to register in an occupied cell. */
    void spawn() {
        if (destroyed) {
            return;
        }
        if (world.isFree(coordinates, this)) {
            world.addObject(this);
        } else {
            resolveCollisionAt(coordinates);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and non-negative");
        }
        if (destroyed) {
            return;
        }
        if (!world.isFree(coordinates, this)) {
            resolveCollisionAt(coordinates);
            return;
        }

        // Inspect every traversed cell, including when a frame takes several steps.
        double remainingSteps = movementProgress + (double) deltaTime / movementDuration;
        while (remainingSteps >= 1d) {
            GridPoint2 next = direction.getAdjacentCoordinates(coordinates);
            if (!world.isFree(next, this)) {
                resolveCollisionAt(next);
                return;
            }
            coordinates.set(next);
            remainingSteps -= 1d;
        }
        movementProgress = (float) remainingSteps;
    }

    private void resolveCollisionAt(GridPoint2 cell) {
        for (GameObject occupant : world.getObjectsAt(cell)) {
            if (occupant == this) {
                continue;
            }
            if (occupant instanceof Tank) {
                ((Tank) occupant).takeDamage(damage);
            } else if (occupant instanceof Bullet) {
                ((Bullet) occupant).destroy();
            }
        }
        destroy();
    }

    private void destroy() {
        if (!destroyed) {
            destroyed = true;
            world.removeObject(this);
        }
    }

    @Override
    public boolean occupies(GridPoint2 cell) {
        return !destroyed && coordinates.equals(cell);
    }

    @Override
    public Vector2 getPositionOnGrid() {
        GridPoint2 next = direction.getAdjacentCoordinates(coordinates);
        return new Vector2(coordinates.x + 0.5f, coordinates.y + 0.5f)
                .lerp(new Vector2(next.x + 0.5f, next.y + 0.5f), movementProgress);
    }

    @Override
    public float getRotationDegrees() {
        return direction.getRotationDegrees();
    }
}
