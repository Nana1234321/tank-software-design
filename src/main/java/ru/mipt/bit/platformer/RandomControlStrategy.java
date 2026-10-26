package ru.mipt.bit.platformer;

import java.util.Objects;
import java.util.Random;

public final class RandomControlStrategy implements ControlStrategy {
    private final Random random;

    public RandomControlStrategy(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Command nextCommand(Movable target) {
        Direction[] directions = Direction.values();
        return new MoveCommand(target, directions[random.nextInt(directions.length)]);
    }
}
