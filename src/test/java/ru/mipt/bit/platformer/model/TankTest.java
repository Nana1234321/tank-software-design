package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class TankTest {
    private Tank createTank(Field field, int x, int y) {
        return new Tank(new GridPoint2(x,y), Direction.RIGHT, field, 1f);
    }
    @Test public void interpolatesReservesAndCompletesStep() {
        Field field = new Field(4,4);
        Tank tank = createTank(field,1,1);
        field.addObject(tank);
        tank.move(Direction.RIGHT);
        assertTrue(tank.occupies(new GridPoint2(1,1)));
        assertTrue(tank.occupies(new GridPoint2(2,1)));
        tank.update(0f);
        assertEquals(1.5f, tank.getPositionOnGrid().x, 0f);
        tank.update(0.25f);
        assertEquals(1.65625f, tank.getPositionOnGrid().x, 0.0001f);
        tank.update(0.25f);
        assertEquals(2f, tank.getPositionOnGrid().x, 0.0001f);
        field.update(5f);
        assertEquals(2.5f, tank.getPositionOnGrid().x, 0f);
        assertFalse(tank.occupies(new GridPoint2(1,1)));
        assertTrue(tank.occupies(new GridPoint2(2,1)));
        field.update(5f);
        assertEquals(2.5f, tank.getPositionOnGrid().x, 0f);
    }
    @Test public void ignoresCommandsDuringStepAndAcceptsNextStep() {
        Field field = new Field(4,4);
        Tank tank = createTank(field,1,1);
        field.addObject(tank);
        tank.move(Direction.RIGHT);
        tank.move(Direction.UP);
        assertEquals(Direction.RIGHT.getRotationDegrees(), tank.getRotationDegrees(), 0f);
        tank.update(1f);
        tank.move(Direction.UP);
        tank.update(1f);
        assertTrue(tank.occupies(new GridPoint2(2,2)));
    }
    @Test public void turnsButDoesNotEnterObstacleOrLeaveField() {
        Field field = new Field(3,3);
        Tank tank = createTank(field,0,0);
        field.addObject(tank);
        field.addObject(new Tree(new GridPoint2(0,1)));
        tank.move(Direction.UP);
        tank.update(1f);
        assertEquals(90f, tank.getRotationDegrees(), 0f);
        assertTrue(tank.occupies(new GridPoint2(0,0)));
        tank.move(Direction.LEFT);
        tank.update(1f);
        assertTrue(tank.occupies(new GridPoint2(0,0)));
        assertFalse(tank.occupies(new GridPoint2(-1,0)));
    }
}
