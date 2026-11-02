package ru.mipt.bit.platformer.model;

/** Receives changes to the objects registered in a logical field. */
public interface FieldListener {
    void onObjectAdded(GameObject object);

    void onObjectRemoved(GameObject object);
}
