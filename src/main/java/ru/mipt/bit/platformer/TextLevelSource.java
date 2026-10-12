package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import java.util.Objects;

/** T = tree, X = player, _ = empty. Short rows are padded on the right. */
public final class TextLevelSource implements LevelSource {
    private final String text;

    public TextLevelSource(String text) {
        this.text = Objects.requireNonNull(text);
    }

    @Override
    public Level load(float movementDuration) {
        String content = text.startsWith("\uFEFF") ? text.substring(1) : text;
        String[] rows = content.split("\\R");
        int width = 0;
        int players = 0;
        for (int row = 0; row < rows.length; row++) {
            if (rows[row].isEmpty()) {
                throw new IllegalArgumentException("Empty level row: " + (row + 1));
            }
            width = Math.max(width, rows[row].length());
            for (int column = 0; column < rows[row].length(); column++) {
                char symbol = rows[row].charAt(column);
                if (symbol == 'X') {
                    players++;
                } else if (symbol != 'T' && symbol != '_') {
                    throw new IllegalArgumentException("Unknown symbol '" + symbol
                            + "' at row " + (row + 1) + ", column " + (column + 1));
                }
            }
        }
        if (width == 0 || players != 1) {
            throw new IllegalArgumentException("Level must contain exactly one X");
        }
        Field field = new Field(width, rows.length);
        Tank player = null;
        for (int row = 0; row < rows.length; row++) {
            for (int column = 0; column < rows[row].length(); column++) {
                GridPoint2 cell = new GridPoint2(column, rows.length - row - 1);
                switch (rows[row].charAt(column)) {
                    case 'T': field.addObject(new Tree(cell)); break;
                    case 'X':
                        player = field.addObject(new Tank(cell, Direction.RIGHT, field, movementDuration));
                        break;
                    default: break;
                }
            }
        }
        return new Level(field, player);
    }
}
