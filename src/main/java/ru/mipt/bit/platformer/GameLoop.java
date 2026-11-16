package ru.mipt.bit.platformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.controller.InputHandler;
import ru.mipt.bit.platformer.model.Field;

/**
 * Выполняет один кадр: собирает команды, выполняет их, обновляет поле и вызывает отрисовку.
 * Это сценарий слоя application.
 * InputHandler — порт получения команд, а renderFrame — переданная снаружи функция отрисовки.
 * Для ясности можно выделить интерфейсы AdvanceFrame и RenderFrame, хотя текущая схема
 * уже позволяет подменять ввод и отрисовку.
 */
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

        // Сначала собираем команды игрока и AI, пока мир еще не изменился.
        List<Command> commands = new ArrayList<>();
        for (InputHandler handler : inputHandlers) {
            Command command = handler.createCommand();
            if (command != null) {
                commands.add(command);
            }
        }

        // Выполняем собранные команды по порядку.
        for (Command command : commands) {
            command.execute();
        }

        // Обновляем объекты на поле.
        field.update(deltaTime);

        // Рисуем кадр после обновления модели.
        renderFrame.run();
    }
}
