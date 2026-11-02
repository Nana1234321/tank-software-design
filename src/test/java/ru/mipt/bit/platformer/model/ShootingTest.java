package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Collections;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.controller.TankController;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class ShootingTest {
    private Tank createTank(Field field, int x, int y, Direction direction) {
        return new Tank(new GridPoint2(x, y), direction, field, 1f);
    }

    private Bullet onlyBullet(Field field) {
        Bullet result = null;
        for (GameObject object : field.getObjects()) {
            if (object instanceof Bullet) {
                assertNull("Expected only one bullet", result);
                result = (Bullet) object;
            }
        }
        assertNotNull("Expected a bullet", result);
        return result;
    }

    @Test public void shootCommandDefersShotUntilExecution() {
        int[] shots = {0};
        Command command = new ShootCommand(() -> shots[0]++);
        assertEquals(0, shots[0]);
        command.execute();
        assertEquals(1, shots[0]);
    }

    @Test public void bulletSpawnsInFrontOfTankInEveryDirection() {
        for (Direction direction : Direction.values()) {
            Field field = new Field(7, 7);
            Tank tank = createTank(field, 3, 3, direction);
            field.addObject(tank);
            tank.shoot();
            Bullet bullet = onlyBullet(field);
            assertEquals(2, field.getObjects().size());
            assertTrue(bullet.occupies(direction.getAdjacentCoordinates(new GridPoint2(3, 3))));
            assertEquals(direction.getRotationDegrees(), bullet.getRotationDegrees(), 0f);
            field.update(0.1f);
            assertTrue(bullet.occupies(direction.getAdjacentCoordinates(
                    direction.getAdjacentCoordinates(new GridPoint2(3, 3)))));
        }
    }

    @Test public void spaceCreatesBulletAndTakesPriorityOverMovement() {
        Field field = new Field(5, 3);
        Tank tank = createTank(field, 1, 1, Direction.RIGHT);
        field.addObject(tank);
        TankController controller = new TankController(tank,
                key -> key == SPACE || key == UP, Collections.singletonMap(UP, Direction.UP));
        Command command = controller.createCommand();
        assertEquals(1, field.getObjects().size());
        command.execute();
        assertFalse(tank.isMoving());
        assertTrue(onlyBullet(field).occupies(new GridPoint2(2, 1)));
    }

    @Test public void heldSpaceRespectsCooldownAndCanFireAgain() {
        Field field = new Field(12, 1);
        Tank tank = createTank(field, 0, 0, Direction.RIGHT);
        field.addObject(tank);
        TankController controller = new TankController(tank,
                key -> key == SPACE, Collections.emptyMap());
        controller.createCommand().execute();
        Bullet first = onlyBullet(field);
        controller.createCommand().execute();
        field.update(0.2f);
        controller.createCommand().execute();
        assertSame(first, onlyBullet(field));
        field.update(0.2f);
        controller.createCommand().execute();
        assertEquals(3, field.getObjects().size());
    }

    @Test public void shotDuringMovementWaitsUntilTankFinishesStep() {
        Field field = new Field(6, 1);
        Tank tank = createTank(field, 0, 0, Direction.RIGHT);
        field.addObject(tank);
        tank.move(Direction.RIGHT);
        tank.update(0.5f);
        tank.shoot();
        assertEquals(1, field.getObjects().size());
        tank.update(0.5f);
        tank.shoot();
        assertTrue(onlyBullet(field).occupies(new GridPoint2(2, 0)));
    }

    @Test public void tankTurningAfterShotDoesNotChangeBulletDirection() {
        Field field = new Field(6, 5);
        Tank tank = createTank(field, 1, 1, Direction.RIGHT);
        field.addObject(tank);
        tank.shoot();
        Bullet bullet = onlyBullet(field);
        tank.move(Direction.UP);
        field.update(0.1f);
        assertTrue(bullet.occupies(new GridPoint2(3, 1)));
        assertEquals(Direction.RIGHT.getRotationDegrees(), bullet.getRotationDegrees(), 0f);
    }

    @Test public void adjacentTankAndTreeAbsorbShotWithoutOverlappingObjects() {
        for (boolean obstacleIsTank : new boolean[]{false, true}) {
            Field field = new Field(4, 1);
            Tank shooter = createTank(field, 0, 0, Direction.RIGHT);
            field.addObject(shooter);
            GameObject obstacle = obstacleIsTank
                    ? createTank(field, 1, 0, Direction.LEFT)
                    : new Tree(new GridPoint2(1, 0));
            field.addObject(obstacle);
            shooter.shoot();
            assertEquals(2, field.getObjects().size());
            assertTrue(field.getObjects().contains(obstacle));
        }
    }

    @Test public void reservedDestinationOfMovingTankAlsoAbsorbsShot() {
        Field field = new Field(4, 1);
        Tank shooter = createTank(field, 0, 0, Direction.RIGHT);
        field.addObject(shooter);
        Tank other = createTank(field, 2, 0, Direction.LEFT);
        field.addObject(other);
        other.move(Direction.LEFT);
        shooter.shoot();
        assertEquals(2, field.getObjects().size());
    }

    @Test public void shootingOutsideFieldIsSafeInEveryDirection() {
        for (Direction direction : Direction.values()) {
            Field field = new Field(1, 1);
            Tank tank = createTank(field, 0, 0, direction);
            field.addObject(tank);
            tank.shoot();
            assertEquals(1, field.getObjects().size());
        }
    }

    @Test public void largeTimeStepCannotSkipObstacleAndRemovalDoesNotBreakUpdate() {
        Field field = new Field(8, 1);
        Tank shooter = createTank(field, 0, 0, Direction.RIGHT);
        field.addObject(shooter);
        Tree tree = new Tree(new GridPoint2(4, 0));
        field.addObject(tree);
        shooter.shoot();
        Bullet bullet = onlyBullet(field);
        field.update(1f);
        assertFalse(field.getObjects().contains(bullet));
        assertTrue(field.getObjects().contains(tree));
    }

    @Test public void flyingBulletDisappearsOnTankAndOnFieldBoundary() {
        for (boolean targetPresent : new boolean[]{false, true}) {
            Field field = new Field(5, 1);
            Tank shooter = createTank(field, 0, 0, Direction.RIGHT);
            field.addObject(shooter);
            if (targetPresent) {
                field.addObject(createTank(field, 4, 0, Direction.LEFT));
            }
            shooter.shoot();
            Bullet bullet = onlyBullet(field);
            field.update(1f);
            assertFalse(field.getObjects().contains(bullet));
            assertEquals(targetPresent ? 2 : 1, field.getObjects().size());
        }
    }

    @Test public void bulletCollisionRemovesBothAndSkipsRemovedBulletInUpdate() {
        Field field = new Field(5, 1);
        Tank left = createTank(field, 0, 0, Direction.RIGHT);
        field.addObject(left);
        Tank right = createTank(field, 4, 0, Direction.LEFT);
        field.addObject(right);
        left.shoot();
        right.shoot();
        field.update(0.1f);
        assertEquals(2, field.getObjects().size());
    }

    @Test public void spawningOnExistingBulletDestroysBoth() {
        Field field = new Field(3, 1);
        Tank left = createTank(field, 0, 0, Direction.RIGHT);
        field.addObject(left);
        Tank right = createTank(field, 2, 0, Direction.LEFT);
        field.addObject(right);
        left.shoot();
        right.shoot();
        assertEquals(2, field.getObjects().size());
    }

    @Test public void bulletInterpolatesAndRejectsInvalidTimeValues() {
        Field field = new Field(6, 1);
        Tank tank = createTank(field, 0, 0, Direction.RIGHT);
        field.addObject(tank);
        tank.shoot();
        Bullet bullet = onlyBullet(field);
        field.update(0.05f);
        assertEquals(2f, bullet.getPositionOnGrid().x, 0.0001f);
        assertThrows(IllegalArgumentException.class, () -> bullet.update(-1f));
        assertThrows(IllegalArgumentException.class, () -> bullet.update(Float.NaN));
        assertThrows(IllegalArgumentException.class, () -> new Bullet(
                new GridPoint2(), Direction.RIGHT, field, 0f));
    }
}
