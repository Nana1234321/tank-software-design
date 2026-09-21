package ru.mipt.bit.platformer;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import static org.junit.Assert.*;

public class FieldTest {
    @Test public void checksAllBounds() {
        Field field = new Field(3,2);
        assertTrue(field.isFree(new GridPoint2(0,0), null));
        assertTrue(field.isFree(new GridPoint2(2,1), null));
        for (GridPoint2 cell : new GridPoint2[]{new GridPoint2(-1,0),new GridPoint2(0,-1),new GridPoint2(3,0),new GridPoint2(0,2)}) {
            assertFalse(field.isFree(cell, null));
            assertThrows(IllegalArgumentException.class, () -> field.spawnTree(cell));
        }
    }
    @Test public void registersObjectsAndExcludesAsker() {
        Field field = new Field(3,3);
        Tank tank = field.spawnTank(new GridPoint2(0,0), Direction.RIGHT, 1f);
        assertEquals(1, field.getObjects().size());
        assertSame(tank, field.getObjects().get(0));
        assertFalse(field.isFree(new GridPoint2(0,0), null));
        assertTrue(field.isFree(new GridPoint2(0,0), tank));
        field.spawnTree(new GridPoint2(1,0));
        assertFalse(field.isFree(new GridPoint2(1,0), tank));
        assertThrows(IllegalArgumentException.class, () -> field.spawnTree(new GridPoint2(1,0)));
        assertThrows(UnsupportedOperationException.class, () -> field.getObjects().clear());
    }
}
