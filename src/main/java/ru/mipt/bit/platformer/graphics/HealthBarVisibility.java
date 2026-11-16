package ru.mipt.bit.platformer.graphics;

/**
 * Хранит настройку показа полос здоровья.
 * Относится к UI в слое infrastructure: на здоровье и правила игры эта настройка не влияет.
 */
public final class HealthBarVisibility {
    private boolean visible;

    public boolean isVisible() {
        return visible;
    }

    public void toggle() {
        visible = !visible;
    }
}
