package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class RandomLevelSource implements LevelSource {
    private final int width;
    private final int height;
    private final int treeCount;
    private final int aiTankCount;
    private final Random random;

    public RandomLevelSource(int width, int height, int treeCount, Random random) {
        this(width, height, treeCount, 0, random);
    }

    public RandomLevelSource(int width, int height, int treeCount, int aiTankCount, Random random) {
        if (width <= 0 || height <= 0 || (long) width * height > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid level dimensions");
        }
        if (treeCount < 0 || aiTankCount < 0
                || (long) treeCount + aiTankCount + 1 > (long) width * height) {
            throw new IllegalArgumentException("Objects must leave a cell for the player");
        }
        this.width = width;
        this.height = height;
        this.treeCount = treeCount;
        this.aiTankCount = aiTankCount;
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Level load(float movementDuration) {
        List<GridPoint2> cells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells.add(new GridPoint2(x, y));
            }
        }
        Collections.shuffle(cells, random);
        Field field = new Field(width, height);
        Tank player = field.addObject(new Tank(cells.get(0), Direction.RIGHT, field, movementDuration));
        for (int i = 1; i <= treeCount; i++) {
            field.addObject(new Tree(cells.get(i)));
        }
        for (int i = 1; i <= aiTankCount; i++) {
            field.addObject(new Tank(cells.get(treeCount + i), Direction.RIGHT, field, movementDuration));
        }
        return new Level(field, player);
    }
}
