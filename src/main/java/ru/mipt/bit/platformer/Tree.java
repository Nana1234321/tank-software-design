package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.util.TileMovement;

public final class Tree extends Obstacle {

    private final GridPoint2 coordinates;

    public Tree(
            String texturePath,
            GridPoint2 coordinates,
            TileMovement tileMovement
    ) {
        super(texturePath);
        this.coordinates = new GridPoint2(coordinates);
        tileMovement.placeAtTileCenter(getRectangle(), this.coordinates);
    }

    @Override
    public boolean occupies(GridPoint2 cellCoordinates) {
        return coordinates.equals(cellCoordinates);
    }

}

