package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.TankActions;

/**
 * Выбирает следующую команду для танка. Интерфейс относится к application.
 * Клавиатурный адаптер получает выбор игрока, а RandomControlStrategy выбирает действие AI.
 * Так управление можно менять, не меняя игровой цикл.
 */
@FunctionalInterface
public interface ControlStrategy {
    /** Возвращает null, если сейчас нет команды. */
    Command nextCommand(TankActions target);
}
