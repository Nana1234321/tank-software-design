package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.TankActions;

@FunctionalInterface
public interface ControlStrategy {
    /** Returns null when no action is requested. */
    Command nextCommand(TankActions target);
}
