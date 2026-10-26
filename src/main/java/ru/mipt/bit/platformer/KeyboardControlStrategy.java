package ru.mipt.bit.platformer;

import java.util.Objects;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;

public final class KeyboardControlStrategy implements ControlStrategy {
    private final IntPredicate isKeyPressed;
    private final Map<Integer, Direction> keyBindings;

    /** Binding iteration order determines priority when several keys are pressed. */
    public KeyboardControlStrategy(IntPredicate isKeyPressed,
                          Map<Integer, Direction> keyBindings) {
        this.isKeyPressed = Objects.requireNonNull(isKeyPressed);
        Map<Integer, Direction> copy = new LinkedHashMap<>();
        Objects.requireNonNull(keyBindings).forEach((key, direction) ->
                copy.put(Objects.requireNonNull(key), Objects.requireNonNull(direction)));
        this.keyBindings = Collections.unmodifiableMap(copy);
    }

    @Override
    public Command nextCommand(Movable target) {
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

