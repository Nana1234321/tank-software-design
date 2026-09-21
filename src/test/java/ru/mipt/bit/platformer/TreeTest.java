package ru.mipt.bit.platformer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;

public class TreeTest {
    @Test public void treeIsStationaryAndProtectsItsPosition() {
        GridPoint2 cell = new GridPoint2(1,2);
        Tree tree = new Field(5,5).spawnTree(cell);
        cell.set(4,4);
        tree.getPositionOnGrid().set(99,99);
        tree.update(10f);
        assertTrue(tree.occupies(new GridPoint2(1,2)));
        assertFalse(tree.occupies(new GridPoint2(4,4)));
        assertEquals(new Vector2(1.5f,2.5f), tree.getPositionOnGrid());
        assertEquals(0f, tree.getRotationDegrees(), 0f);
    }
}
