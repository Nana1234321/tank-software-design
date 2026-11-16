package ru.mipt.bit.platformer.model;

/**
 * Операции танка, доступные его состояниям.
 * Это внутренний порт domain. Приватный Tank.StateContext связывает его с самим танком,
 * не открывая эти операции остальному коду.
 */
public interface TankStateContext {
    int getHealth();

    int getMaxHealth();

    void changeState(TankState state);

    /** Продвигает танк с заданным множителем скорости. Множитель должен быть конечным и больше нуля. */
    void advanceMovement(float deltaTime, float speedMultiplier);

    /** Стреляет, если танк готов. Множитель перезарядки должен быть конечным и больше нуля. */
    void fire(float cooldownMultiplier);
}
