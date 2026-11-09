package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ru.mipt.bit.platformer.model.GameObject;

import static ru.mipt.bit.platformer.graphics.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.graphics.GdxGameUtils.drawTextureRegionUnscaled;

public final class GameObjectRenderer implements ObjectRenderer {
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    public GameObjectRenderer(TextureRegion graphics) {
        this.graphics = new TextureRegion(graphics);
        rectangle = createBoundingRectangle(this.graphics);
    }

    @Override
    public void render(Batch batch, GameObject object, float tileWidth, float tileHeight) {
        Vector2 center = object.getPositionOnGrid().scl(tileWidth, tileHeight);
        rectangle.setCenter(center);
        drawTextureRegionUnscaled(batch, graphics, rectangle, object.getRotationDegrees());
    }
}
