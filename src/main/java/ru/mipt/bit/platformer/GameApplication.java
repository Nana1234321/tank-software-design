package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.Objects;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public final class GameApplication extends ApplicationAdapter {
    private static final float TANK_MOVEMENT_DURATION = 0.4f;
    private final List<InputHandler> inputHandlers = new ArrayList<>();
    private Batch batch;
    private Field field;
    private FieldRenderer renderer;
    private FitViewport viewport;
    private final LevelSource levelSource;

    public GameApplication() {
        this(new RandomLevelSource(10, 8, 20, new Random()));
    }

    public GameApplication(LevelSource levelSource) {
        this.levelSource = Objects.requireNonNull(levelSource);
    }

    @Override
    public void create() {
        Level level = levelSource.load(TANK_MOVEMENT_DURATION);
        field = level.getField();
        batch = new SpriteBatch();
        renderer = new FieldRenderer("level.tmx", batch, field.getWidth(), field.getHeight());
        viewport = new FitViewport(renderer.getPixelWidth(), renderer.getPixelHeight(), new OrthographicCamera());
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.registerAppearance(Tank.class, "images/tank_blue.png");
        renderer.registerAppearance(Tree.class, "images/greenTree.png");
        createControls(level.getPlayer());
    }

    private void createControls(Tank tank) {
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(UP, Direction.UP);
        bindings.put(W, Direction.UP);
        bindings.put(LEFT, Direction.LEFT);
        bindings.put(A, Direction.LEFT);
        bindings.put(DOWN, Direction.DOWN);
        bindings.put(S, Direction.DOWN);
        bindings.put(RIGHT, Direction.RIGHT);
        bindings.put(D, Direction.RIGHT);
        inputHandlers.add(new TankController(tank, key -> Gdx.input.isKeyPressed(key), bindings));
    }

    @Override
    public void render() {
        for (InputHandler handler : inputHandlers) {
            handler.handleInput();
        }
        field.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        viewport.apply();
        renderer.setView((OrthographicCamera) viewport.getCamera());
        renderer.render(field);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();
    }
}
