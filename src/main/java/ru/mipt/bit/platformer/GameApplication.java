package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.List;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public final class GameApplication extends ApplicationAdapter {
    private static final float TANK_MOVEMENT_DURATION = 0.4f;
    private final List<InputHandler> inputHandlers = new ArrayList<>();
    private Batch batch;
    private Field field;
    private FieldRenderer renderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        renderer = new FieldRenderer("level.tmx", batch);
        renderer.registerAppearance(Tank.class, "images/tank_blue.png");
        renderer.registerAppearance(Tree.class, "images/greenTree.png");
        createScene();
    }

    private void createScene() {
        field = new Field(renderer.getWidth(), renderer.getHeight());
        Tank tank = field.spawnTank(new GridPoint2(1, 1), Direction.RIGHT, TANK_MOVEMENT_DURATION);
        field.spawnTree(new GridPoint2(1, 3));
        inputHandlers.add(new TankController(tank, key -> Gdx.input.isKeyPressed(key)));
    }

    @Override
    public void render() {
        for (InputHandler handler : inputHandlers) {
            handler.handleInput();
        }
        field.update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        renderer.render(field);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();
    }
}
