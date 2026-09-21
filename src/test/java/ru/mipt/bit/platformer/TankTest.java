package ru.mipt.bit.platformer;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import static org.junit.Assert.*;

public class TankTest {
    private Tank spawn(Field field, int x, int y) {
        return field.spawnTank(new GridPoint2(x,y), Direction.RIGHT, 1f);
    }
    @Test public void interpolatesReservesAndCompletesStep() {
        Field field = new Field(4,4);
        Tank tank = spawn(field,1,1);
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
        Tank tank = spawn(new Field(4,4),1,1);
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
        Tank tank = spawn(field,0,0);
        field.spawnTree(new GridPoint2(0,1));
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
