package ru.mipt.bit.platformer.model;

/**
 * Объединяет движение и стрельбу в один интерфейс для управления танком.
 * Это входной API сущности из domain. Реализован в Tank и используется командами
 * и стратегиями управления.
 */
public interface TankActions extends Movable, Shootable {
}
