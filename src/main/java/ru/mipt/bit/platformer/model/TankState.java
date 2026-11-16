package ru.mipt.bit.platformer.model;

/**
 * Поведение танка в текущем состоянии: движение, стрельба и реакция на урон.
 * Относится к domain и реализует идею паттерна State.
 * Состояния работают через TankStateContext, поэтому им не нужны ни LibGDX, ни игровой мир.
 */
public interface TankState {
    default void updateMovement(TankStateContext context, float deltaTime) {
        context.advanceMovement(deltaTime, 1f);
    }

    default void shoot(TankStateContext context) {
        context.fire(1f);
    }

    /** Новое состояние может переопределить реакцию на урон. По умолчанию ничего не происходит. */
    default void onHealthChanged(TankStateContext context) {
    }
}
