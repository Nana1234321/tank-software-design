package ru.mipt.bit.platformer.command;

import java.util.Objects;
import ru.mipt.bit.platformer.model.Shootable;

public final class ShootCommand implements Command {
    private final Shootable target;

    public ShootCommand(Shootable target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public void execute() {
        target.shoot();
    }
}
