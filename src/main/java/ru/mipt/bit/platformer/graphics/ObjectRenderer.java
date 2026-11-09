package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.model.GameObject;

@FunctionalInterface
public interface ObjectRenderer {
    void render(Batch batch, GameObject object, float tileWidth, float tileHeight);

    /** Drawn after all object appearances so overlays cannot be covered by later sprites. */
    default void renderOverlay(Batch batch, GameObject object, float tileWidth, float tileHeight) {
    }
}
