package ru.mipt.bit.platformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.controller.InputHandler;
import ru.mipt.bit.platformer.model.Field;

/** Runs one frame in the order: collect commands, execute, update, render. */
public final class GameLoop {
    private final Field field;
    private final List<InputHandler> inputHandlers;
    private final Runnable renderFrame;

    public GameLoop(Field field, List<? extends InputHandler> inputHandlers,
                    Runnable renderFrame) {
        this.field = Objects.requireNonNull(field);
        this.inputHandlers = new ArrayList<>(Objects.requireNonNull(inputHandlers));
        this.inputHandlers.forEach(Objects::requireNonNull);
        this.renderFrame = Objects.requireNonNull(renderFrame);
    }

    public void runFrame(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and non-negative");
        }

        // 1. Collect all player and AI commands before changing the world.
        List<Command> commands = new ArrayList<>();
        for (InputHandler handler : inputHandlers) {
            Command command = handler.createCommand();
            if (command != null) {
                commands.add(command);
            }
        }

        // 2. Execute only the commands collected for this frame, in collection order.
        for (Command command : commands) {
            command.execute();
        }

        // 3. Advance the logical state of every registered object.
        field.update(deltaTime);

        // 4. Draw the resulting state, including all observer updates.
        renderFrame.run();
    }
}
