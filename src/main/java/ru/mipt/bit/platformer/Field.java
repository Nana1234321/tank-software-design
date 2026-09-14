package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.util.TileMovement;

import java.util.ArrayList;
import java.util.List;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

public final class Field implements Disposable {

    private final TiledMap map;
    private final MapRenderer mapRenderer;
    private final TiledMapTileLayer groundLayer;
    private final TileMovement tileMovement;
    private final List<Obstacle> obstacles = new ArrayList<>();

    public Field(String mapPath, Batch batch) {
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = createSingleLayerMapRenderer(map, batch);
        groundLayer = getSingleLayer(map);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    public TileMovement getTileMovement() {
        return tileMovement;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public boolean isBlocked(GridPoint2 coordinates) {
        if (isOutside(coordinates)) {
            return true;
        }

        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupies(coordinates)) {
                return true;
            }
        }
        return false;
    }

    public void renderGround() {
        mapRenderer.render();
    }

    public void renderObstacles(Batch batch) {
        for (Obstacle obstacle : obstacles) {
            obstacle.render(batch);
        }
    }

    private boolean isOutside(GridPoint2 coordinates) {
        return coordinates.x < 0
                || coordinates.y < 0
                || coordinates.x >= groundLayer.getWidth()
                || coordinates.y >= groundLayer.getHeight();
    }

    @Override
    public void dispose() {
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        map.dispose();
    }
}

