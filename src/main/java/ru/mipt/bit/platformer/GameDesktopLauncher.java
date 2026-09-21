package ru.mipt.bit.platformer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public final class GameDesktopLauncher {

    private GameDesktopLauncher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config =
                new Lwjgl3ApplicationConfiguration();

        // Level width: 10 tiles x 128px, height: 8 tiles x 128px.
        config.setWindowedMode(1280, 1024);

        new Lwjgl3Application(new GameApplication(), config);
    }
}
