package ru.mipt.bit.platformer;

import java.util.Objects;

public final class Level {
    private final Field field;
    private final Tank player;

    public Level(Field field, Tank player) {
        this.field = Objects.requireNonNull(field);
        this.player = Objects.requireNonNull(player);
        if (!field.getObjects().contains(player)) {
            throw new IllegalArgumentException("Player must belong to the level field");
        }
    }

    public Field getField() { return field; }
    public Tank getPlayer() { return player; }
}
