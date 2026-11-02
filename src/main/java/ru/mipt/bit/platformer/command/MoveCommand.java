package ru.mipt.bit.platformer.command;

import java.util.Objects;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Movable;

public final class MoveCommand implements Command {
    private final Movable target;
    private final Direction direction;

    public MoveCommand(Movable target, Direction direction) {
        this.target = Objects.requireNonNull(target);
        this.direction = Objects.requireNonNull(direction);
    }

    @Override
    public void execute() {
        target.move(direction);
    }
}
