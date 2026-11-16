package ru.mipt.bit.platformer.model;

/**
 * Общие переходы между состояниями по оставшемуся здоровью.
 * Часть правил domain; используется легким, средним и тяжелым состояниями.
 */
public abstract class HealthTankState implements TankState {
    @Override
    public void onHealthChanged(TankStateContext context) {
        context.changeState(forHealth(context.getHealth(), context.getMaxHealth()));
    }

    public static TankState forHealth(int health, int initialHealth) {
        // Сравниваем целые числа: так не теряем точность на границах и не делим на ноль.
        if (initialHealth == 0 || (long) health * 100 < (long) initialHealth * 15) {
            return new HeavyTankState();
        }
        if ((long) health * 100 <= (long) initialHealth * 70) {
            return new MediumTankState();
        }
        return new LightTankState();
    }
}
