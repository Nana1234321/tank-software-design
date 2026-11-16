package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import java.util.Objects;

/**
 * Танк: хранит здоровье, движется и стреляет. Это сущность слоя domain.
 * Доступные действия описаны в TankActions, а поведение зависит от TankState.
 * Для полной независимости модели от LibGDX нужно заменить его типы координат
 * и Interpolation собственными вычислениями.
 */
public final class Tank extends GameObject implements TankActions {

    private static final int DEFAULT_HEALTH = 100;
    private static final float BULLET_MOVEMENT_DURATION = 0.2f;
    private static final float SHOT_COOLDOWN = 0.4f;

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private final GameWorld world;
    private final float movementDuration;
    private final int bulletDamage;
    private final int maxHealth;

    private int health;
    private TankState state;
    private final TankStateContext stateContext = new StateContext();
    private Direction direction;
    private float movementProgress = 1f;
    private float timeUntilNextShot;

    public Tank(
            GridPoint2 initialCoordinates,
            Direction initialDirection,
            GameWorld world,
            float movementDuration
    ) {
        this(initialCoordinates, initialDirection, world, movementDuration,
                DEFAULT_HEALTH, Bullet.DEFAULT_DAMAGE);
    }

    public Tank(
            GridPoint2 initialCoordinates,
            Direction initialDirection,
            GameWorld world,
            float movementDuration,
            int initialHealth,
            int bulletDamage
    ) {
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be finite and positive");
        }
        if (initialHealth < 0 || bulletDamage < 0) {
            throw new IllegalArgumentException("Initial health and bullet damage must be non-negative");
        }
        this.coordinates = new GridPoint2(initialCoordinates);
        this.destinationCoordinates = new GridPoint2(initialCoordinates);
        this.direction = Objects.requireNonNull(initialDirection);
        this.world = Objects.requireNonNull(world);
        this.movementDuration = movementDuration;
        this.health = initialHealth;
        this.maxHealth = initialHealth;
        this.bulletDamage = bulletDamage;
        this.state = HealthTankState.forHealth(health, maxHealth);
    }

    @Override
    public void move(Direction newDirection) {
        if (!isAlive() || isMoving()) {
            return;
        }

        direction = Objects.requireNonNull(newDirection);

        GridPoint2 adjacentCoordinates =
                newDirection.getAdjacentCoordinates(coordinates);

        if (world.isFree(adjacentCoordinates, this)) {
            destinationCoordinates.set(adjacentCoordinates);
            movementProgress = 0f;
        }
    }

    @Override
    public void shoot() {
        if (isAlive()) {
            state.shoot(stateContext);
        }
    }

    private void fire(float cooldownMultiplier) {
        // Стрелять можно между шагами, когда танк закончил движение в следующую клетку.
        if (!isAlive() || isMoving() || timeUntilNextShot > 0f) {
            return;
        }
        Bullet bullet = new Bullet(direction.getAdjacentCoordinates(coordinates),
                direction, world, BULLET_MOVEMENT_DURATION, bulletDamage);
        bullet.spawn();
        timeUntilNextShot = SHOT_COOLDOWN * cooldownMultiplier;
    }

    public TankState getState() {
        return state;
    }

    public void setState(TankState state) {
        this.state = Objects.requireNonNull(state);
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /** По условию танк уничтожается при здоровье ниже нуля. При нуле он еще жив. */
    public boolean isAlive() {
        return health >= 0;
    }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be non-negative");
        }
        if (!isAlive()) {
            return;
        }
        health -= damage;
        if (!isAlive()) {
            world.removeObject(this);
        } else {
            state.onHealthChanged(stateContext);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and non-negative");
        }
        if (!isAlive()) {
            return;
        }
        timeUntilNextShot = Math.max(0f, timeUntilNextShot - deltaTime);
        if (!isMoving()) {
            return;
        }

        state.updateMovement(stateContext, deltaTime);
    }

    private void advanceMovement(float deltaTime, float speedMultiplier) {
        movementProgress = Math.min(1f,
                movementProgress + deltaTime * speedMultiplier / movementDuration);

        if (!isMoving()) {
            coordinates.set(destinationCoordinates);
        }
    }

    @Override
    public Vector2 getPositionOnGrid() {
        Vector2 start = new Vector2(coordinates.x + 0.5f, coordinates.y + 0.5f);
        Vector2 destination = new Vector2(
                destinationCoordinates.x + 0.5f, destinationCoordinates.y + 0.5f
        );
        return start.interpolate(destination, movementProgress, Interpolation.smooth);
    }

    @Override
    public boolean occupies(GridPoint2 cellCoordinates) {
        return isAlive() && (coordinates.equals(cellCoordinates)
                || (isMoving() && destinationCoordinates.equals(cellCoordinates)));
    }

    @Override
    public float getRotationDegrees() {
        return direction.getRotationDegrees();
    }

    public boolean isMoving() {
        return isAlive() && movementProgress < 1f;
    }

    /**
     * Связывает операции TankStateContext с этим танком внутри domain.
     * Состояния могут вызвать эти операции, а остальной код их не видит.
     */
    private final class StateContext implements TankStateContext {
        @Override public int getHealth() {
            return Tank.this.getHealth();
        }

        @Override public int getMaxHealth() {
            return Tank.this.getMaxHealth();
        }

        @Override public void changeState(TankState nextState) {
            setState(nextState);
        }

        @Override public void advanceMovement(float deltaTime, float speedMultiplier) {
            Tank.this.advanceMovement(deltaTime, speedMultiplier);
        }

        @Override public void fire(float cooldownMultiplier) {
            Tank.this.fire(cooldownMultiplier);
        }
    }

}
