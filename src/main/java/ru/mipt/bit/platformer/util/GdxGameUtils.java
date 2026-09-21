package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.NoSuchElementException;


public final class GdxGameUtils {

    private GdxGameUtils() {
    }

    public static OrthogonalTiledMapRenderer createSingleLayerMapRenderer(TiledMap tiledMap, Batch batch) {
        TiledMapTileLayer tileLayer = getSingleLayer(tiledMap);
        float viewWidth = tileLayer.getWidth() * tileLayer.getTileWidth();
        float viewHeight = tileLayer.getHeight() * tileLayer.getTileHeight();

        OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        mapRenderer.getViewBounds().set(0f, 0f, viewWidth, viewHeight);

        return mapRenderer;
    }

    public static <L extends MapLayer> L getSingleLayer(Map map) {
        MapLayers layers = map.getLayers();
        switch (layers.size()) {
            case 0:
                throw new NoSuchElementException("Map has no layers");
            case 1:
                @SuppressWarnings("unchecked")
                L layer = (L) layers.iterator().next();
                return layer;
            default:
                throw new IllegalArgumentException("Map has more than one layer");
        }
    }

    public static void drawTextureRegionUnscaled(Batch batch, TextureRegion region, Rectangle rectangle, float rotation) {
        int regionWidth = region.getRegionWidth();
        int regionHeight = region.getRegionHeight();
        float regionOriginX = regionWidth / 2f;
        float regionOriginY = regionHeight / 2f;
        batch.draw(region, rectangle.x, rectangle.y, regionOriginX, regionOriginY, regionWidth, regionHeight, 1f, 1f, rotation);
    }

    public static Rectangle createBoundingRectangle(TextureRegion region) {
        return new Rectangle()
                .setWidth(region.getRegionWidth())
                .setHeight(region.getRegionHeight());
    }

}
