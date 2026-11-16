package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.controller.TankController;

import static org.junit.Assert.*;

public class DamageTest {
    private Tank createTank(Field field, int x, int health, int bulletDamage) {
        return new Tank(new GridPoint2(x, 0), Direction.RIGHT,
                field, 1f, health, bulletDamage);
    }

    @Test public void flyingShotUsesConfiguredDamage() {
        Field field = new Field(6, 1);
        Tank shooter = createTank(field, 0, 100, 35);
        field.addObject(shooter);
        Tank target = createTank(field, 4, 100, 10);
        field.addObject(target);
        shooter.shoot();
        Bullet bullet = (Bullet) field.getObjectsAt(new GridPoint2(1, 0)).get(0);
        assertEquals(35, bullet.getDamage());
        field.update(0.6f);
        assertEquals(65, target.getHealth());
        assertEquals(100, shooter.getHealth());
        assertFalse(field.getObjects().contains(bullet));
        assertEquals(2, field.getObjects().size());
    }

    @Test public void pointBlankShotUsesDefaultDamageOnlyOnce() {
        Field field = new Field(3, 1);
        Tank shooter = new Tank(new GridPoint2(0, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(shooter);
        Tank target = new Tank(new GridPoint2(1, 0),
                Direction.LEFT, field, 1f);
        field.addObject(target);
        assertEquals(100, target.getHealth());
        shooter.shoot();
        assertEquals(75, target.getHealth());
        field.update(1f);
        assertEquals(75, target.getHealth());
        assertEquals(2, field.getObjects().size());
    }

    @Test public void zeroHealthRemainsAliveUntilAnotherHit() {
        Field field = new Field(3, 1);
        Tank shooter = createTank(field, 0, 100, 10);
        field.addObject(shooter);
        Tank target = createTank(field, 1, 10, 10);
        field.addObject(target);
        shooter.shoot();
        assertEquals(0, target.getHealth());
        assertTrue(target.isAlive());
        assertTrue(field.getObjects().contains(target));
        field.update(0.4f);
        shooter.shoot();
        assertEquals(-10, target.getHealth());
        assertFalse(target.isAlive());
        assertFalse(field.getObjects().contains(target));
        assertTrue(field.isFree(new GridPoint2(1, 0), null));
    }

    @Test public void lethalShotDoesNotPassThroughFirstTankDuringLargeFrame() {
        Field field = new Field(8, 1);
        Tank shooter = createTank(field, 0, 100, 25);
        field.addObject(shooter);
        Tank first = createTank(field, 4, 20, 10);
        field.addObject(first);
        Tank second = createTank(field, 6, 100, 10);
        field.addObject(second);
        shooter.shoot();
        field.update(1f);
        assertEquals(-5, first.getHealth());
        assertFalse(field.getObjects().contains(first));
        assertEquals(100, second.getHealth());
        assertEquals(2, field.getObjects().size());
    }

    @Test public void pointBlankLethalHitRemovesTargetWithoutRegisteringBullet() {
        Field field = new Field(2, 1);
        Tank shooter = createTank(field, 0, 100, 10);
        field.addObject(shooter);
        Tank target = createTank(field, 1, 0, 10);
        field.addObject(target);
        shooter.shoot();
        assertEquals(-10, target.getHealth());
        assertEquals(1, field.getObjects().size());
        assertSame(shooter, field.getObjects().get(0));
    }

    @Test public void lethalHitOnMovingTankReleasesBothReservedCells() {
        Field field = new Field(6, 1);
        Tank shooter = createTank(field, 0, 100, 25);
        field.addObject(shooter);
        Tank target = createTank(field, 3, 20, 10);
        field.addObject(target);
        target.move(Direction.LEFT);
        assertTrue(target.occupies(new GridPoint2(2, 0)));
        assertTrue(target.occupies(new GridPoint2(3, 0)));
        shooter.shoot();
        field.update(0.2f);
        assertEquals(-5, target.getHealth());
        assertFalse(field.getObjects().contains(target));
        assertTrue(field.isFree(new GridPoint2(2, 0), null));
        assertTrue(field.isFree(new GridPoint2(3, 0), null));
    }

    @Test public void deadTankIgnoresControllerAndPreviouslyCreatedCommands() {
        Field field = new Field(4, 3);
        Tank tank = new Tank(new GridPoint2(1, 1),
                Direction.RIGHT, field, 1f, 10, 25);
        field.addObject(tank);
        Command move = new MoveCommand(tank, Direction.UP);
        Command shoot = new ShootCommand(tank);
        int[] choices = {0};
        TankController controller = new TankController(tank, target -> {
            choices[0]++;
            return new ShootCommand(target);
        });
        tank.move(Direction.RIGHT);
        tank.update(0.5f);
        Vector2 lastPosition = tank.getPositionOnGrid();
        tank.takeDamage(11);
        move.execute();
        shoot.execute();
        assertNull(controller.createCommand());
        tank.update(1f);
        field.update(1f);
        assertEquals(0, choices[0]);
        assertTrue(field.getObjects().isEmpty());
        assertEquals(lastPosition, tank.getPositionOnGrid());
        assertFalse(tank.isMoving());
        assertFalse(tank.occupies(new GridPoint2(1, 1)));
        assertFalse(tank.occupies(new GridPoint2(2, 1)));
        tank.takeDamage(Integer.MAX_VALUE);
        assertEquals(-1, tank.getHealth());
    }

    @Test public void spentBulletCannotDealDamageAgainOrBeRespawned() {
        Field field = new Field(4, 1);
        Tank target = createTank(field, 1, 100, 10);
        field.addObject(target);
        Bullet bullet = new Bullet(new GridPoint2(1, 0), Direction.RIGHT,
                field, 0.1f, 30);
        bullet.spawn();
        assertEquals(70, target.getHealth());
        bullet.spawn();
        bullet.update(10f);
        assertEquals(70, target.getHealth());
        assertEquals(1, field.getObjects().size());
    }

    @Test public void projectileCollisionRemovesBothWithoutDamagingTanks() {
        Field field = new Field(5, 1);
        Tank left = createTank(field, 0, 100, 25);
        field.addObject(left);
        Tank right = new Tank(new GridPoint2(4, 0),
                Direction.LEFT, field, 1f, 100, 25);
        field.addObject(right);
        left.shoot();
        right.shoot();
        field.update(0.2f);
        assertEquals(100, left.getHealth());
        assertEquals(100, right.getHealth());
        assertEquals(2, field.getObjects().size());
    }

    @Test public void negativeDamageIsRejectedAndZeroDamageStillConsumesBullet() {
        Field field = new Field(3, 1);
        Tank target = createTank(field, 1, 100, 10);
        field.addObject(target);
        assertThrows(IllegalArgumentException.class, () -> target.takeDamage(-10));
        assertThrows(IllegalArgumentException.class, () -> new Tank(
                new GridPoint2(), Direction.RIGHT, field, 1f, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> new Tank(
                new GridPoint2(), Direction.RIGHT, field, 1f, 100, -1));
        assertThrows(IllegalArgumentException.class, () -> new Bullet(
                new GridPoint2(), Direction.RIGHT, field, 0.1f, -1));
        Bullet bullet = new Bullet(new GridPoint2(1, 0), Direction.RIGHT,
                field, 0.1f, 0);
        bullet.spawn();
        assertEquals(100, target.getHealth());
        assertFalse(field.getObjects().contains(bullet));
    }
}
