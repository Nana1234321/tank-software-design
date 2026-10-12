package ru.mipt.bit.platformer;
import com.badlogic.gdx.math.GridPoint2;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.badlogic.gdx.Input.Keys.*;

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
            Tank tank = field.addObject(new Tank(new GridPoint2(2,2), Direction.RIGHT, field, 1f));
            final int key = keys[i];
            InputHandler handler = new TankController(tank, pressed -> pressed == key, bindings);
            handler.handleInput();
            tank.update(1f);
            assertTrue(tank.occupies(directions[i].getAdjacentCoordinates(new GridPoint2(2,2))));
        }
    }
    @Test public void noKeyDoesNotStartMovement() {
        Field field = new Field(3,3);
        Tank tank = field.addObject(new Tank(new GridPoint2(1,1), Direction.RIGHT, field, 1f));
        new TankController(tank, key -> false, java.util.Collections.singletonMap(UP, Direction.UP)).handleInput();
        tank.update(1f);
        assertTrue(tank.occupies(new GridPoint2(1,1)));
    }
}
