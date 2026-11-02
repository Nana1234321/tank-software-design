package ru.mipt.bit.platformer.controller;

import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

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
    public Command createCommand() {
        if (!target.isAlive() || target.isMoving()) {
            return null;
        }
        return strategy.nextCommand(target);
    }
}
