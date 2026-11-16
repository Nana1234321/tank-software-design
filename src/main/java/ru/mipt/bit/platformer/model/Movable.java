package ru.mipt.bit.platformer.model;

/**
 * Объект игровой модели, которому можно передать команду движения.
 * Интерфейс domain: им пользуется MoveCommand, а реализует его Tank.
 */
@FunctionalInterface
public interface Movable {
    void move(Direction direction);
}
