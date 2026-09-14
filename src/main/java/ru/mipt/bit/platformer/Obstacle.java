package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

public abstract class Obstacle extends GameObject {

    protected Obstacle(String texturePath) {
        super(texturePath);
    }

    public abstract boolean occupies(GridPoint2 coordinates);
}

