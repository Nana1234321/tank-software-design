package ru.mipt.bit.platformer.controller;

import com.badlogic.gdx.math.GridPoint2;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class TankControllerTest {
    @Test public void mapsEverySupportedKey() {
        int[] keys = {UP,W,LEFT,A,DOWN,S,RIGHT,D};
        Direction[] directions = {Direction.UP,Direction.UP,Direction.LEFT,Direction.LEFT,Direction.DOWN,Direction.DOWN,Direction.RIGHT,Direction.RIGHT};
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        for (int i=0; i<keys.length; i++) {
            bindings.put(keys[i], directions[i]);
        }
        for (int i=0; i<keys.length; i++) {
            Field field = new Field(5,5);
            Tank tank = new Tank(new GridPoint2(2,2), Direction.RIGHT, field, 1f);
            field.addObject(tank);
            final int key = keys[i];
            InputHandler handler = new TankController(tank, pressed -> pressed == key, bindings);
            Command command = handler.createCommand();
            assertNotNull(command);
            assertFalse(tank.isMoving());
            command.execute();
            tank.update(1f);
            assertTrue(tank.occupies(directions[i].getAdjacentCoordinates(new GridPoint2(2,2))));
        }
    }
    @Test public void noKeyDoesNotStartMovement() {
        Field field = new Field(3,3);
        Tank tank = new Tank(new GridPoint2(1,1), Direction.RIGHT, field, 1f);
        field.addObject(tank);
        assertNull(new TankController(tank, key -> false,
                java.util.Collections.singletonMap(UP, Direction.UP)).createCommand());
        tank.update(1f);
        assertTrue(tank.occupies(new GridPoint2(1,1)));
    }
}
