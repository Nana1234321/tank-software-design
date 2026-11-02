package ru.mipt.bit.platformer.controller;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Random;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Level;
import ru.mipt.bit.platformer.model.RandomLevelSource;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.TankActions;

import static org.junit.Assert.*;

public class AiControlTest {
    @Test public void commandDefersMovementUntilExecution() {
        Direction[] received = {null};
        Command command = new MoveCommand(direction -> received[0] = direction, Direction.UP);
        assertNull(received[0]);
        command.execute();
        assertEquals(Direction.UP, received[0]);
    }

    @Test public void randomStrategyChoosesMovementAndShooting() {
        Random expected = new Random(42);
        ControlStrategy strategy = new RandomControlStrategy(new Random(42));
        Direction[] received = {null};
        int[] shots = {0};
        TankActions target = new TankActions() {
            @Override public void move(Direction direction) { received[0] = direction; }
            @Override public void shoot() { shots[0]++; }
        };
        for (int i = 0; i < 20; i++) {
            received[0] = null;
            int previousShots = shots[0];
            int action = expected.nextInt(Direction.values().length + 1);
            strategy.nextCommand(target).execute();
            if (action == Direction.values().length) {
                assertNull(received[0]);
                assertEquals(previousShots + 1, shots[0]);
            } else {
                assertEquals(Direction.values()[action], received[0]);
                assertEquals(previousShots, shots[0]);
            }
        }
        assertTrue(shots[0] > 0);
    }

    @Test public void controllerChoosesOncePerStep() {
        Field field = new Field(4, 1);
        Tank tank = new Tank(new GridPoint2(0, 0), Direction.RIGHT, field, 1f);
        field.addObject(tank);
        int[] choices = {0};
        TankController controller = new TankController(tank, target -> {
            choices[0]++;
            return new MoveCommand(target, Direction.RIGHT);
        });
        controller.createCommand().execute();
        tank.update(0.5f);
        assertNull(controller.createCommand());
        assertEquals(1, choices[0]);
        tank.update(0.5f);
        controller.createCommand().execute();
        assertEquals(2, choices[0]);
        assertTrue(tank.occupies(new GridPoint2(2, 0)));
    }

    @Test public void otherTanksCannotEnterEitherReservedCell() {
        Field field = new Field(4, 2);
        Tank moving = new Tank(new GridPoint2(1, 0), Direction.RIGHT, field, 1f);
        field.addObject(moving);
        Tank behind = new Tank(new GridPoint2(0, 0), Direction.RIGHT, field, 1f);
        field.addObject(behind);
        Tank ahead = new Tank(new GridPoint2(3, 0), Direction.LEFT, field, 1f);
        field.addObject(ahead);
        moving.move(Direction.RIGHT);
        behind.move(Direction.RIGHT);
        ahead.move(Direction.LEFT);
        assertFalse(behind.isMoving());
        assertFalse(ahead.isMoving());
        moving.update(1f);
        behind.move(Direction.RIGHT);
        assertTrue(behind.isMoving());
        ahead.move(Direction.LEFT);
        assertFalse(ahead.isMoving());
    }

    @Test public void generatesRequestedObjectsWithoutOverlap() {
        Level level = new RandomLevelSource(4, 3, 4, 7, new Random(42)).load(1f);
        int tanks = 0;
        assertTrue(level.getField().getObjects().contains(level.getPlayer()));
        for (GameObject object : level.getField().getObjects()) {
            if (object instanceof Tank) tanks++;
        }
        assertEquals(8, tanks);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 3; y++) {
                int occupants = 0;
                for (GameObject object : level.getField().getObjects()) {
                    if (object.occupies(new GridPoint2(x, y))) occupants++;
                }
                assertEquals(1, occupants);
            }
        }
    }

}
