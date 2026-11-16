package ru.mipt.bit.platformer.model;

/**
 * Выходной порт уведомлений о добавлении и удалении объектов поля.
 * Принадлежит domain; FieldRenderer реализует его на стороне графики.
 * Сейчас передаются сами игровые объекты. Для отправки событий по сети понадобятся снимки данных.
 */
public interface FieldListener {
    void onObjectAdded(GameObject object);

    void onObjectRemoved(GameObject object);
}
