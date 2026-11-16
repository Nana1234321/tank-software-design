package ru.mipt.bit.platformer.model;

/**
 * Тяжелое состояние: меньше 15% начального здоровья.
 * Танк движется втрое медленнее и не стреляет. Часть правил domain.
 */
public final class HeavyTankState extends HealthTankState {
    @Override
    public void updateMovement(TankStateContext context, float deltaTime) {
        context.advanceMovement(deltaTime, 1f / 3f);
    }

    @Override
    public void shoot(TankStateContext context) {
        // В тяжелом состоянии выстрел пропускается.
    }
}
