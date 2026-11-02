package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import ru.mipt.bit.platformer.model.FieldListener;
import ru.mipt.bit.platformer.model.GameObject;

import static ru.mipt.bit.platformer.graphics.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.graphics.GdxGameUtils.getSingleLayer;

public final class FieldRenderer implements Disposable, FieldListener {
    private final TiledMap map;
    private final TiledMapTileLayer ground;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Batch batch;
    private final Map<String, Texture> textures = new HashMap<>();
    private final List<Texture> generatedTextures = new ArrayList<>();
    private final Map<Class<? extends GameObject>, GameObjectRenderer> renderers = new HashMap<>();
    private final Set<GameObject> objects = new LinkedHashSet<>();

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

    public void registerSolidAppearance(Class<? extends GameObject> type,
                                        int width, int height, Color color) {
        Pixmap image = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        try {
            image.setColor(color);
            image.fill();
            Texture texture = new Texture(image);
            generatedTextures.add(texture);
            renderers.put(type, new GameObjectRenderer(new TextureRegion(texture)));
        } finally {
            image.dispose();
        }
    }

    @Override
    public void onObjectAdded(GameObject object) {
        objects.add(Objects.requireNonNull(object));
    }

    @Override
    public void onObjectRemoved(GameObject object) {
        objects.remove(Objects.requireNonNull(object));
    }

    public void render() {
        mapRenderer.render();
        batch.begin();
        try {
            for (GameObject object : objects) {
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
        objects.clear();
        mapRenderer.dispose();
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        for (Texture texture : generatedTextures) {
            texture.dispose();
        }
        map.dispose();
    }
}
