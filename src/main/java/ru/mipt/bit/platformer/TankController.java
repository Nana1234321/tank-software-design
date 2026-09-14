package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input;

import static com.badlogic.gdx.Input.Keys.A;
import static com.badlogic.gdx.Input.Keys.D;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.S;
import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.W;

public final class TankController {

    private final Input input;
    private final Tank tank;
    private final Field field;

    public TankController(
            Input input,
            Tank tank,
            Field field
    ) {
        this.input = input;
        this.tank = tank;
        this.field = field;
    }

    public void handleInput() {
        Direction direction = getPressedDirection();

        if (direction != null) {
            tank.tryMove(direction, field);
        }
    }

    private Direction getPressedDirection() {
        if (input.isKeyPressed(UP) || input.isKeyPressed(W)) {
            return Direction.UP;
        }

        if (input.isKeyPressed(LEFT) || input.isKeyPressed(A)) {
            return Direction.LEFT;
        }

        if (input.isKeyPressed(DOWN) || input.isKeyPressed(S)) {
            return Direction.DOWN;
        }

        if (input.isKeyPressed(RIGHT) || input.isKeyPressed(D)) {
            return Direction.RIGHT;
        }

        return null;
    }
}
