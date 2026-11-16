package ru.mipt.bit.platformer.model;

/**
 * Среднее состояние: от 15% до 70% начального здоровья включительно.
 * Танк движется вдвое медленнее. Часть правил domain.
 */
public final class MediumTankState extends HealthTankState {
    @Override
    public void updateMovement(TankStateContext context, float deltaTime) {
        context.advanceMovement(deltaTime, 0.5f);
    }
}
