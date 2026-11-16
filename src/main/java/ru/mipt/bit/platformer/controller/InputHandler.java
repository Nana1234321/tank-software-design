package ru.mipt.bit.platformer.controller;

import ru.mipt.bit.platformer.command.Command;

/**
 * Порт, через который GameLoop получает очередную команду.
 * Относится к application, хотя лежит в пакете controller.
 * Его реализуют TankController и лямбда переключения полос здоровья.
 * Порт выходной: игровой цикл сам запрашивает команду у источника ввода.
 */
@FunctionalInterface
public interface InputHandler {
    /** Создает команду, но не выполняет ее. Если команды нет, возвращает null. */
    Command createCommand();
}
