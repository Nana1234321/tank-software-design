package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

/**
 * Базовый класс объектов игрового мира. Относится к domain.
 * Пока координаты в его API представлены типами LibGDX.
 * Чтобы отделить модель от движка, нужны свои типы клетки и позиции,
 * а перевод в графические координаты должен остаться в адаптере отрисовки.
 */
public abstract class GameObject {
    public abstract boolean occupies(GridPoint2 cell);

    /** Возвращает новую позицию центра в клетках. Ее изменение не сдвигает сам объект. */
    public abstract Vector2 getPositionOnGrid();

    public float getRotationDegrees() {
        return 0f;
    }

    public void update(float deltaTime) {
    }
}
