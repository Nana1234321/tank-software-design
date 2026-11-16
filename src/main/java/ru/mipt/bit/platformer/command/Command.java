package ru.mipt.bit.platformer.command;

/**
 * Общая команда, которую выполняет GameLoop. Относится к application.
 * Игровые действия представлены MoveCommand и ShootCommand.
 * Этот же интерфейс используется для переключения полос здоровья в UI.
 */
@FunctionalInterface
public interface Command {
    void execute();
}
