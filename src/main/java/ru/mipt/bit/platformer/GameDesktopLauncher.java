package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {

    private static final float TANK_MOVEMENT_DURATION = 0.4f;

    private Batch batch;

    private Field field;
    private Tank tank;
    private TankController tankController;

    @Override
    public void create() {
        batch = new SpriteBatch();

        field = new Field("level.tmx", batch);

        tank = new Tank(
                "images/tank_blue.png",
                new GridPoint2(1, 1),
                Direction.RIGHT,
                field.getTileMovement(),
                TANK_MOVEMENT_DURATION
        );

        field.addObstacle(new Tree(
                "images/greenTree.png",
                new GridPoint2(1, 3),
                field.getTileMovement()
        ));

        tankController = new TankController(
                Gdx.input,
                tank,
                field
        );
    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        // get time passed since the last render
        float deltaTime = Gdx.graphics.getDeltaTime();

        tankController.handleInput();
        tank.update(deltaTime);

        field.renderGround();

        // start recording all drawing commands
        batch.begin();

        tank.render(batch);
        field.renderObstacles(batch);

        // submit all drawing requests
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        tank.dispose();
        field.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config =
                new Lwjgl3ApplicationConfiguration();

        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);

        new Lwjgl3Application(
                new GameDesktopLauncher(),
                config
        );
    }
}
