package ru.mipt.bit.platformer.model;

/**
 * Выходной порт загрузки или создания уровня.
 * По смыслу относится к application, хотя сейчас лежит в model.
 * Реализации — TextLevelSource и RandomLevelSource. Пока вызывается из GameApplication;
 * позже загрузку можно перенести в отдельный сценарий начала игры.
 */
@FunctionalInterface
public interface LevelSource {
    Level load(float movementDuration);
}
