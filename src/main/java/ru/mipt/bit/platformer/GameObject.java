package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawTextureRegionUnscaled;

public abstract class GameObject implements Disposable {

    private final Texture texture;
    private final TextureRegion graphics;
    private final Rectangle rectangle;

    protected GameObject(String texturePath) {
        texture = new Texture(texturePath);
        graphics = new TextureRegion(texture);
        rectangle = createBoundingRectangle(graphics);
    }

    protected final Rectangle getRectangle() {
        return rectangle;
    }

    protected float getRotationDegrees() {
        return 0f;
    }

    public final void render(Batch batch) {
        drawTextureRegionUnscaled(
                batch,
                graphics,
                rectangle,
                getRotationDegrees()
        );
    }

    @Override
    public final void dispose() {
        texture.dispose();
    }
}

