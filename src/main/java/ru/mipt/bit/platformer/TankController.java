package ru.mipt.bit.platformer;

import java.util.Objects;
import java.util.function.IntPredicate;
import static com.badlogic.gdx.Input.Keys.*;

public final class TankController implements InputHandler {
    private final Tank tank;
    private final IntPredicate isKeyPressed;

    public TankController(Tank tank, IntPredicate isKeyPressed) {
        this.tank = Objects.requireNonNull(tank);
        this.isKeyPressed = Objects.requireNonNull(isKeyPressed);
    }

    @Override
    public void handleInput() {
        Direction direction = getPressedDirection();
        if (direction != null) {
            tank.move(direction);
        }
    }

    private Direction getPressedDirection() {
        if (isKeyPressed.test(UP) || isKeyPressed.test(W)) return Direction.UP;
        if (isKeyPressed.test(LEFT) || isKeyPressed.test(A)) return Direction.LEFT;
        if (isKeyPressed.test(DOWN) || isKeyPressed.test(S)) return Direction.DOWN;
        if (isKeyPressed.test(RIGHT) || isKeyPressed.test(D)) return Direction.RIGHT;
        return null;
    }
}
