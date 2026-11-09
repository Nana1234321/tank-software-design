package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.Objects;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Tank;

/** Adds health bars to the wrapped appearance without changing the game model. */
public final class HealthBarRendererDecorator implements ObjectRenderer {
    private final ObjectRenderer delegate;
    private final HealthBarVisibility visibility;
    private final TextureRegion pixel;

    public HealthBarRendererDecorator(ObjectRenderer delegate, HealthBarVisibility visibility,
                                     TextureRegion pixel) {
        this.delegate = Objects.requireNonNull(delegate);
        this.visibility = Objects.requireNonNull(visibility);
        this.pixel = Objects.requireNonNull(pixel);
    }

    @Override
    public void render(Batch batch, GameObject object, float tileWidth, float tileHeight) {
        delegate.render(batch, object, tileWidth, tileHeight);
    }

    @Override
    public void renderOverlay(Batch batch, GameObject object, float tileWidth, float tileHeight) {
        delegate.renderOverlay(batch, object, tileWidth, tileHeight);
        if (!visibility.isVisible() || !(object instanceof Tank)) {
            return;
        }
        Tank tank = (Tank) object;
        if (!tank.isAlive()) {
            return;
        }
        Vector2 center = tank.getPositionOnGrid().scl(tileWidth, tileHeight);
        float width = tileWidth * 0.7f;
        float height = Math.max(2f, tileHeight * 0.08f);
        float x = center.x - width / 2f;
        float y = center.y + tileHeight * 0.32f;
        float fraction = tank.getMaxHealth() == 0 ? 0f
                : Math.max(0f, Math.min(1f, (float) tank.getHealth() / tank.getMaxHealth()));
        float previousColor = batch.getPackedColor();
        try {
            batch.setColor(Color.DARK_GRAY);
            batch.draw(pixel, x, y, width, height);
            batch.setColor(Color.GREEN);
            if (fraction > 0f) {
                batch.draw(pixel, x, y, width * fraction, height);
            }
        } finally {
            batch.setPackedColor(previousColor);
        }
    }
}
