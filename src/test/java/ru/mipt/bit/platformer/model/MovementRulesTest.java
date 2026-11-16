package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Collections;
import java.util.Random;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.controller.RandomControlStrategy;
import ru.mipt.bit.platformer.controller.TankController;

import static org.junit.Assert.*;

public class MovementRulesTest {
    private Tank createTank(Field field, int x, int y) {
        return new Tank(new GridPoint2(x, y), Direction.RIGHT, field, 1f);
    }

    @Test public void cannotCrossAnyBoundary() {
        for (Direction direction : Direction.values()) {
            Field field = new Field(1, 1);
            Tank tank = createTank(field, 0, 0);
            field.addObject(tank);
            tank.move(direction);
            field.update(2f);
            assertFalse(tank.isMoving());
            assertTrue(tank.occupies(new GridPoint2()));
            assertEquals(direction.getRotationDegrees(), tank.getRotationDegrees(), 0f);
        }
    }

    @Test public void twoTanksCannotStartMovingIntoSameCell() {
        Field field = new Field(3, 1);
        Tank left = createTank(field, 0, 0);
        field.addObject(left);
        Tank right = createTank(field, 2, 0);
        field.addObject(right);
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
        Tank player = createTank(field, 0, 0);
        field.addObject(player);
        Tank ai = createTank(field, 2, 2);
        field.addObject(ai);
        TankController keyboard = new TankController(player, key -> key == 1,
                Collections.singletonMap(1, Direction.UP));
        Random expected = new Random(42);
        TankController computer = new TankController(ai, new RandomControlStrategy(new Random(42)));
        Command playerCommand = keyboard.createCommand();
        Command aiCommand = computer.createCommand();
        assertFalse(player.isMoving());
        assertFalse(ai.isMoving());
        playerCommand.execute();
        aiCommand.execute();
        field.update(1f);
        assertTrue(player.occupies(new GridPoint2(0, 1)));
        assertTrue(ai.occupies(Direction.values()[expected.nextInt(5)]
                .getAdjacentCoordinates(new GridPoint2(2, 2))));
    }

}
