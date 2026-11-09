package ru.mipt.bit.platformer.command;

import java.util.Objects;
import ru.mipt.bit.platformer.graphics.HealthBarVisibility;

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
