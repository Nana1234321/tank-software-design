package ru.mipt.bit.platformer;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import static org.junit.Assert.*;

public class DirectionTest {
    @Test public void directionsHaveCorrectOffsetsAndRotations() {
        Direction[] directions = {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};
        int[][] offsets = {{0,1},{-1,0},{0,-1},{1,0}};
        float[] angles = {90,-180,-90,0};
        GridPoint2 origin = new GridPoint2(4,5);
        for (int i=0; i<directions.length; i++) {
            assertEquals(new GridPoint2(4+offsets[i][0],5+offsets[i][1]), directions[i].getAdjacentCoordinates(origin));
            assertEquals(angles[i], directions[i].getRotationDegrees(), 0f);
        }
        assertEquals(new GridPoint2(4,5), origin);
        Direction.UP.getAdjacentCoordinates(origin).set(99,99);
        assertEquals(new GridPoint2(4,6), Direction.UP.getAdjacentCoordinates(origin));
    }
}
