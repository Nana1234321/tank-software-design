package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import static org.junit.Assert.*;

public class LevelSourceTest {
    @Test public void loadsTextWithBomLineBreaksAndShortRows() {
        Level level = new TextLevelSource("\uFEFF_T\r\nX").load(1f);
        Field field = level.getField();
        assertEquals(2, field.getWidth());
        assertEquals(2, field.getHeight());
        assertEquals(2, field.getObjects().size());
        assertTrue(level.getPlayer().occupies(new GridPoint2(0, 0)));
        assertFalse(field.isFree(new GridPoint2(1, 1), null));
        assertTrue(field.isFree(new GridPoint2(1, 0), null));
        level.getPlayer().move(Direction.RIGHT);
        field.update(1f);
        assertTrue(level.getPlayer().occupies(new GridPoint2(1, 0)));
    }

    @Test public void rejectsMalformedText() {
        for (String text : new String[]{"", "__", "XX", "X?", "X\n\n_"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new TextLevelSource(text).load(1f));
        }
    }

    @Test public void playerMustBelongToField() {
        Field field = new Field(2, 2);
        Tank player = new Tank(new GridPoint2(), Direction.RIGHT, field, 1f);
        assertThrows(IllegalArgumentException.class, () -> new Level(field, player));
        field.addObject(player);
        Level level = new Level(field, player);
        assertSame(field, level.getField());
        assertSame(player, level.getPlayer());
    }

}
