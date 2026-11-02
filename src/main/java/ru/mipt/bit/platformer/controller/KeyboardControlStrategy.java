package ru.mipt.bit.platformer.controller;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankActions;

import static com.badlogic.gdx.Input.Keys.SPACE;

public final class KeyboardControlStrategy implements ControlStrategy {
    private final IntPredicate isKeyPressed;
    private final Map<Integer, Direction> keyBindings;

    /** Space takes priority; movement bindings are checked in iteration order. */
    public KeyboardControlStrategy(IntPredicate isKeyPressed,
                          Map<Integer, Direction> keyBindings) {
        this.isKeyPressed = Objects.requireNonNull(isKeyPressed);
        Map<Integer, Direction> copy = new LinkedHashMap<>();
        Objects.requireNonNull(keyBindings).forEach((key, direction) ->
                copy.put(Objects.requireNonNull(key), Objects.requireNonNull(direction)));
        this.keyBindings = Collections.unmodifiableMap(copy);
    }

    @Override
    public Command nextCommand(TankActions target) {
        if (isKeyPressed.test(SPACE)) {
            return new ShootCommand(target);
        }
        Direction direction = getPressedDirection();
        if (direction != null) {
            return new MoveCommand(target, direction);
        }
        return null;
    }

    private Direction getPressedDirection() {
        for (Map.Entry<Integer, Direction> binding : keyBindings.entrySet()) {
            if (isKeyPressed.test(binding.getKey())) {
                return binding.getValue();
            }
        }
        return null;
    }
}

