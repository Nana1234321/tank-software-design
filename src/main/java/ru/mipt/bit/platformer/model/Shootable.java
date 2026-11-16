package ru.mipt.bit.platformer.model;

/**
 * Объект игровой модели, который умеет стрелять.
 * Интерфейс domain: ShootCommand вызывает его, а Tank выполняет выстрел.
 */
@FunctionalInterface
public interface Shootable {
    void shoot();
}
