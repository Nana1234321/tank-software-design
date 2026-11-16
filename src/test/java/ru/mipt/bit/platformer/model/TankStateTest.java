package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class TankStateTest {
    private Tank createTank(Field field, int initialHealth) {
        Tank tank = new Tank(new GridPoint2(0, 0), Direction.RIGHT,
                field, 1f, initialHealth, 10);
        field.addObject(tank);
        return tank;
    }

    @Test public void transitionsAtExactPercentagesOfInitialHealth() {
        Tank tank = createTank(new Field(4, 1), 200);
        assertTrue(tank.getState() instanceof LightTankState);
        tank.takeDamage(59);
        assertTrue(tank.getState() instanceof LightTankState);
        tank.takeDamage(1);
        assertTrue(tank.getState() instanceof MediumTankState);
        tank.takeDamage(110);
        assertTrue(tank.getState() instanceof MediumTankState);
        tank.takeDamage(1);
        assertTrue(tank.getState() instanceof HeavyTankState);
    }

    @Test public void movementTakesOneTwoOrThreeTimesTheBaseDuration() {
        int[] health = {71, 70, 15, 14, 0};
        float[] durations = {1f, 2f, 2f, 3f, 3f};
        for (int i = 0; i < health.length; i++) {
            Tank tank = createTank(new Field(4, 1), 100);
            tank.takeDamage(100 - health[i]);
            tank.move(Direction.RIGHT);
            tank.update(durations[i] / 2f);
            assertTrue(tank.isMoving());
            assertEquals(1f, tank.getPositionOnGrid().x, 0.0001f);
            tank.update(durations[i] / 2f);
            assertFalse(tank.isMoving());
            assertEquals(1.5f, tank.getPositionOnGrid().x, 0f);
        }
    }

    @Test public void onlyHeavyStateDisablesShooting() {
        for (int health : new int[]{100, 70, 15, 14, 0}) {
            Field field = new Field(4, 1);
            Tank tank = createTank(field, 100);
            tank.takeDamage(100 - health);
            tank.shoot();
            assertEquals(health < 15 ? 1 : 2, field.getObjects().size());
        }
    }

    @Test public void newStateCanDoubleMovementAndFiringRate() {
        Field field = new Field(10, 1);
        Tank tank = createTank(field, 100);
        tank.setState(new HealthTankState() {
            @Override public void updateMovement(TankStateContext context, float deltaTime) {
                context.advanceMovement(deltaTime, 2f);
            }

            @Override public void shoot(TankStateContext context) {
                context.fire(0.5f);
            }
        });
        tank.move(Direction.RIGHT);
        tank.update(0.25f);
        assertEquals(1f, tank.getPositionOnGrid().x, 0.0001f);
        tank.update(0.25f);
        assertFalse(tank.isMoving());
        tank.shoot();
        field.update(0.1f);
        tank.shoot();
        assertEquals(2, field.getObjects().size());
        field.update(0.1f);
        tank.shoot();
        assertEquals(3, field.getObjects().size());
        tank.takeDamage(90);
        assertTrue(tank.getState() instanceof HeavyTankState);
    }

}
