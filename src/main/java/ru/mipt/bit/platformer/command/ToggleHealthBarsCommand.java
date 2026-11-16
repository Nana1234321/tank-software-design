package ru.mipt.bit.platformer.command;

import java.util.Objects;
import ru.mipt.bit.platformer.graphics.HealthBarVisibility;

/**
 * Включает или скрывает полосы здоровья.
 * Это UI-команда: она связывает Command с HealthBarVisibility и относится к infrastructure.
 * По смыслу ей место рядом с графикой, хотя сейчас она лежит вместе с игровыми командами.
 */
public final class ToggleHealthBarsCommand implements Command {
    private final HealthBarVisibility visibility;

    public ToggleHealthBarsCommand(HealthBarVisibility visibility) {
        this.visibility = Objects.requireNonNull(visibility);
    }

    @Override
    public void execute() {
        visibility.toggle();
    }
}
