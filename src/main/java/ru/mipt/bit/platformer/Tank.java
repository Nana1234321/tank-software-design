package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.continueProgress;

public final class Tank extends GameObject {

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private final TileMovement tileMovement;
    private final float movementDuration;

    private Direction direction;
    private float movementProgress = 1f;

    public Tank(
            String texturePath,
            GridPoint2 initialCoordinates,
            Direction initialDirection,
            TileMovement tileMovement,
            float movementDuration
    ) {
        super(texturePath);
        this.coordinates = new GridPoint2(initialCoordinates);
        this.destinationCoordinates = new GridPoint2(initialCoordinates);
        this.direction = initialDirection;
        this.tileMovement = tileMovement;
        this.movementDuration = movementDuration;

        tileMovement.moveRectangleBetweenTileCenters(
                getRectangle(),
                coordinates,
                destinationCoordinates,
                movementProgress
        );
    }

    public void tryMove(Direction newDirection, Field field) {
        if (isMoving()) {
            return;
        }

        direction = newDirection;

        GridPoint2 adjacentCoordinates =
                newDirection.getAdjacentCoordinates(coordinates);

        if (!field.isBlocked(adjacentCoordinates)) {
            destinationCoordinates.set(adjacentCoordinates);
            movementProgress = 0f;
        }
    }

    public void update(float deltaTime) {
        tileMovement.moveRectangleBetweenTileCenters(
                getRectangle(),
                coordinates,
                destinationCoordinates,
                movementProgress
        );

        movementProgress = continueProgress(
                movementProgress,
                deltaTime,
                movementDuration
        );

        if (!isMoving()) {
            coordinates.set(destinationCoordinates);
        }
    }

    @Override
    protected float getRotationDegrees() {
        return direction.getRotationDegrees();
    }

    private boolean isMoving() {
        return !isEqual(movementProgress, 1f);
    }
}
