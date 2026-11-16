package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.model.GameObject;

/**
 * Описывает, как нарисовать один объект.
 * Это внутренний интерфейс графики (infrastructure), а не порт игровой логики:
 * в его методах используется Batch из LibGDX.
 */
@FunctionalInterface
public interface ObjectRenderer {
    void render(Batch batch, GameObject object, float tileWidth, float tileHeight);

    /** Рисуется поверх всех объектов, чтобы другие спрайты не закрывали полосы здоровья. */
    default void renderOverlay(Batch batch, GameObject object, float tileWidth, float tileHeight) {
    }
}
