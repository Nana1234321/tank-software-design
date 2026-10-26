package ru.mipt.bit.platformer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameObjectTest {
    @Test public void defaultBehaviorAllowsStaticObjects() {
        GameObject object = new GameObject() {
            public boolean occupies(GridPoint2 cell) { return cell.x == 0 && cell.y == 0; }
            public Vector2 getPositionOnGrid() { return new Vector2(0.5f,0.5f); }
        };
        object.update(10f);
        assertEquals(0f, object.getRotationDegrees(), 0f);
        assertEquals(new Vector2(0.5f,0.5f), object.getPositionOnGrid());
        assertTrue(object.occupies(new GridPoint2()));
    }
}
