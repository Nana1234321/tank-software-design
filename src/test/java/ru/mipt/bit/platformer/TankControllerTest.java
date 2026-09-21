package ru.mipt.bit.platformer;
import com.badlogic.gdx.math.GridPoint2;


import org.junit.Test;
import static org.junit.Assert.*;
import static com.badlogic.gdx.Input.Keys.*;

public class TankControllerTest {
    @Test public void mapsEverySupportedKey() {
        int[] keys = {UP,W,LEFT,A,DOWN,S,RIGHT,D};
        Direction[] directions = {Direction.UP,Direction.UP,Direction.LEFT,Direction.LEFT,Direction.DOWN,Direction.DOWN,Direction.RIGHT,Direction.RIGHT};
        for (int i=0; i<keys.length; i++) {
            Tank tank = new Field(5,5).spawnTank(new GridPoint2(2,2), Direction.RIGHT, 1f);
            final int key = keys[i];
            InputHandler handler = new TankController(tank, pressed -> pressed == key);
            handler.handleInput();
            tank.update(1f);
            assertTrue(tank.occupies(directions[i].getAdjacentCoordinates(new GridPoint2(2,2))));
        }
    }
    @Test public void noKeyDoesNotStartMovement() {
        Tank tank = new Field(3,3).spawnTank(new GridPoint2(1,1), Direction.RIGHT, 1f);
        new TankController(tank, key -> false).handleInput();
        tank.update(1f);
        assertTrue(tank.occupies(new GridPoint2(1,1)));
    }
}
