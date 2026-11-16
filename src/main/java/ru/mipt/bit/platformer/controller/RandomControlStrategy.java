package ru.mipt.bit.platformer.controller;

import java.util.Objects;
import java.util.Random;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.TankActions;

/**
 * Выбирает случайное действие для AI-танка.
 * Относится к application и реализует ControlStrategy.
 * Внешнего ввода здесь нет: это простая логика управления ботом.
 */
public final class RandomControlStrategy implements ControlStrategy {
    private final Random random;

    public RandomControlStrategy(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Command nextCommand(TankActions target) {
        Direction[] directions = Direction.values();
        int action = random.nextInt(directions.length + 1);
        if (action == directions.length) {
            return new ShootCommand(target);
        }
        return new MoveCommand(target, directions[action]);
    }
}
