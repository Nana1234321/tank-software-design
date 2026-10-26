package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Collections;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class MovementRulesTest {
    private Tank spawn(Field field, int x, int y) {
        return field.addObject(new Tank(new GridPoint2(x, y), Direction.RIGHT, field, 1f));
    }

    @Test public void cannotCrossAnyBoundary() {
        for (Direction direction : Direction.values()) {
            Field field = new Field(1, 1);
            Tank tank = spawn(field, 0, 0);
            tank.move(direction);
            field.update(2f);
            assertFalse(tank.isMoving());
            assertTrue(tank.occupies(new GridPoint2()));
            assertEquals(direction.getRotationDegrees(), tank.getRotationDegrees(), 0f);
        }
    }

    @Test public void twoTanksCannotStartMovingIntoSameCell() {
        Field field = new Field(3, 1);
        Tank left = spawn(field, 0, 0);
        Tank right = spawn(field, 2, 0);
        left.move(Direction.RIGHT);
        right.move(Direction.LEFT);
        assertTrue(left.isMoving());
        assertFalse(right.isMoving());
        field.update(1f);
        assertTrue(left.occupies(new GridPoint2(1, 0)));
        assertTrue(right.occupies(new GridPoint2(2, 0)));
    }

    @Test public void keyboardAndAiControlDifferentTanks() {
        Field field = new Field(5, 5);
        Tank player = spawn(field, 0, 0);
        Tank ai = spawn(field, 2, 2);
        TankController keyboard = new TankController(player, key -> true,
                Collections.singletonMap(1, Direction.UP));
        Random expected = new Random(42);
        TankController computer = new TankController(ai, new RandomControlStrategy(new Random(42)));
        keyboard.handleInput();
        assertFalse(ai.isMoving());
        computer.handleInput();
        field.update(1f);
        assertTrue(player.occupies(new GridPoint2(0, 1)));
        assertTrue(ai.occupies(Direction.values()[expected.nextInt(4)]
                .getAdjacentCoordinates(new GridPoint2(2, 2))));
    }

}
