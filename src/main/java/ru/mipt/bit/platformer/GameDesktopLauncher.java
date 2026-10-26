package ru.mipt.bit.platformer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public final class GameDesktopLauncher {

    private GameDesktopLauncher() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            throw new IllegalArgumentException("Usage: GameDesktopLauncher [level-file | --random]");
        }
        GameApplication game = args.length == 0 || "--random".equals(args[0])
                ? new GameApplication()
                : new GameApplication(new TextLevelSource(new String(
                        Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8)));
        Lwjgl3ApplicationConfiguration config =
                new Lwjgl3ApplicationConfiguration();

        // Level width: 10 tiles x 128px, height: 8 tiles x 128px.
        config.setWindowedMode(1280, 1024);

        new Lwjgl3Application(game, config);
    }
}
