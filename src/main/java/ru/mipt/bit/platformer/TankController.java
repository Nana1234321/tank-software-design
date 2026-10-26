package ru.mipt.bit.platformer;

import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class TankController implements InputHandler {
    private final Tank target;
    private final ControlStrategy strategy;

    public TankController(Tank target, ControlStrategy strategy) {
        this.target = Objects.requireNonNull(target);
        this.strategy = Objects.requireNonNull(strategy);
    }

    public TankController(Tank target, IntPredicate isKeyPressed,
                          Map<Integer, Direction> keyBindings) {
        this(target, new KeyboardControlStrategy(isKeyPressed, keyBindings));
    }

    @Override
    public void handleInput() {
        if (target.isMoving()) {
            return;
        }
        Command command = strategy.nextCommand(target);
        if (command != null) {
            command.execute();
        }
    }
}
