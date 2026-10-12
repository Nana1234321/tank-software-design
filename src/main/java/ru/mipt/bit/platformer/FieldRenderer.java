package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;


public final class FieldRenderer implements Disposable {
    private final TiledMap map;
    private final TiledMapTileLayer ground;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Batch batch;
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<Class<? extends GameObject>, GameObjectRenderer> renderers = new HashMap<>();

    public FieldRenderer(String mapPath, Batch batch) {
        this(mapPath, batch, 0, 0);
    }

    public FieldRenderer(String mapPath, Batch batch, int width, int height) {
        this.batch = batch;
        map = new TmxMapLoader().load(mapPath);
        TiledMapTileLayer template = getSingleLayer(map);
        if (width > 0 && height > 0) {
            ground = new TiledMapTileLayer(width, height, template.getTileWidth(), template.getTileHeight());
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    ground.setCell(x, y, template.getCell(x % template.getWidth(), y % template.getHeight()));
                }
            }
            map.getLayers().remove(template);
            map.getLayers().add(ground);
        } else {
            ground = template;
        }
        mapRenderer = createSingleLayerMapRenderer(map, batch);
    }

    public int getPixelWidth() { return getWidth() * ground.getTileWidth(); }
    public int getPixelHeight() { return getHeight() * ground.getTileHeight(); }

    public void setView(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    public int getWidth() {
        return ground.getWidth();
    }

    public int getHeight() {
        return ground.getHeight();
    }

    public void registerAppearance(Class<? extends GameObject> type, String texturePath) {
        Texture texture = textures.computeIfAbsent(texturePath, Texture::new);
        renderers.put(type, new GameObjectRenderer(new TextureRegion(texture)));
    }

    public void render(Field field) {
        mapRenderer.render();
        batch.begin();
        try {
            for (GameObject object : field.getObjects()) {
                GameObjectRenderer renderer = renderers.get(object.getClass());
                if (renderer == null) {
                    throw new IllegalStateException("No appearance for " + object.getClass().getSimpleName());
                }
                renderer.render(batch, object, ground.getTileWidth(), ground.getTileHeight());
            }
        } finally {
            batch.end();
        }
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        map.dispose();
    }
}
