package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.controller.InputHandler;
import ru.mipt.bit.platformer.controller.TankController;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.FieldListener;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Tank;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class GameLoopTest {
    @Test public void allControllersChooseBeforeAnyCommandChangesTheWorld() {
        Field field = new Field(5, 2);
        Tank player = new Tank(new GridPoint2(0, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(player);
        Tank ai = new Tank(new GridPoint2(3, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(ai);
        InputHandler keyboard = new TankController(player, key -> key == UP,
                Collections.singletonMap(UP, Direction.UP));
        int[] aiChoices = {0};
        InputHandler computer = new TankController(ai, target -> {
            aiChoices[0]++;
            assertFalse(player.isMoving());
            assertTrue(field.isFree(new GridPoint2(0, 1), null));
            return new MoveCommand(target, Direction.LEFT);
        });
        int[] renderedFrames = {0};
        GameLoop loop = new GameLoop(field, Arrays.asList(keyboard, computer), () -> {
            assertTrue(player.isMoving());
            assertTrue(ai.isMoving());
            assertEquals(1f, player.getPositionOnGrid().y, 0.0001f);
            assertEquals(3f, ai.getPositionOnGrid().x, 0.0001f);
            renderedFrames[0]++;
        });
        loop.runFrame(0.5f);
        assertEquals(1, aiChoices[0]);
        assertEquals(1, renderedFrames[0]);
    }

    @Test public void renderingSeesBulletCreationFlightDamageAndObserverRemovals() {
        Field field = new Field(6, 1);
        Tank shooter = new Tank(new GridPoint2(0, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(shooter);
        Tank target = new Tank(new GridPoint2(4, 0),
                Direction.LEFT, field, 1f, 20, 25);
        field.addObject(target);
        Set<GameObject> visible = new LinkedHashSet<>();
        field.addListener(new FieldListener() {
            @Override public void onObjectAdded(GameObject object) { visible.add(object); }
            @Override public void onObjectRemoved(GameObject object) { visible.remove(object); }
        });
        InputHandler keyboard = new TankController(shooter, key -> key == SPACE,
                Collections.emptyMap());
        int[] renderedFrames = {0};
        GameLoop loop = new GameLoop(field, Collections.singletonList(keyboard), () -> {
            assertEquals(-5, target.getHealth());
            assertEquals(Collections.singletonList(shooter), field.getObjects());
            assertEquals(Collections.singleton(shooter), visible);
            renderedFrames[0]++;
        });
        loop.runFrame(0.6f);
        assertEquals(1, renderedFrames[0]);
    }

    @Test public void emptyCommandFramesStillUpdateAndRenderWithoutRepeatingOldCommands() {
        Field field = new Field(1, 1);
        float[] elapsed = {0f};
        int[] updates = {0};
        field.addObject(new GameObject() {
            @Override public boolean occupies(GridPoint2 cell) { return cell.equals(new GridPoint2()); }
            @Override public Vector2 getPositionOnGrid() { return new Vector2(0.5f, 0.5f); }
            @Override public void update(float deltaTime) {
                elapsed[0] += deltaTime;
                updates[0]++;
            }
        });
        int[] polls = {0};
        int[] executions = {0};
        InputHandler input = () -> {
            polls[0]++;
            return polls[0] == 1 ? () -> executions[0]++ : null;
        };
        int[] renderedFrames = {0};
        GameLoop loop = new GameLoop(field, Collections.singletonList(input), () -> {
            renderedFrames[0]++;
            assertEquals(renderedFrames[0], updates[0]);
            assertEquals(1, executions[0]);
        });
        loop.runFrame(0.25f);
        loop.runFrame(0.5f);
        assertEquals(2, polls[0]);
        assertEquals(2, renderedFrames[0]);
        assertEquals(0.75f, elapsed[0], 0f);
    }

    @Test public void commandCollectedForTankKilledEarlierInExecutionIsIgnored() {
        Field field = new Field(3, 1);
        Tank shooter = new Tank(new GridPoint2(0, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(shooter);
        Tank target = new Tank(new GridPoint2(1, 0),
                Direction.RIGHT, field, 1f, 20, 25);
        field.addObject(target);
        InputHandler first = new TankController(shooter, ShootCommand::new);
        int[] targetChoices = {0};
        InputHandler second = new TankController(target, actions -> {
            assertTrue(target.isAlive());
            targetChoices[0]++;
            return new ShootCommand(actions);
        });
        GameLoop loop = new GameLoop(field, Arrays.asList(first, second), () ->
                assertEquals(Collections.singletonList(shooter), field.getObjects()));
        loop.runFrame(0f);
        assertEquals(1, targetChoices[0]);
        assertFalse(target.isAlive());
    }

    @Test public void invalidTickIsRejectedBeforeCollectingOrExecutingCommands() {
        Field field = new Field(1, 1);
        int[] polls = {0};
        InputHandler input = () -> {
            polls[0]++;
            return () -> fail("Invalid frame must not execute commands");
        };
        GameLoop loop = new GameLoop(field, Collections.singletonList(input), () ->
                fail("Invalid frame must not render"));
        for (float tick : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> loop.runFrame(tick));
        }
        assertEquals(0, polls[0]);
    }
}
