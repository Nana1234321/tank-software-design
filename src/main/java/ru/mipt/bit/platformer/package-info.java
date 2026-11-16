/**
 * Игра про танки. Ниже описано, где находятся игровые правила,
 * как выполняются команды и как модель связана с вводом и графикой.
 *
 * <h2>Слои игры</h2>
 * <ul>
 *   <li>Domain — сам игровой мир: GameObject, Tank, Bullet, Tree, Field, Level,
 *       Direction и состояния танка. Здесь находятся здоровье, движение и столкновения.</li>
 *   <li>Application — действия игры: GameLoop выполняет кадр, MoveCommand и ShootCommand
 *       передают команды модели, RandomControlStrategy выбирает действия AI.</li>
 *   <li>Infrastructure — связь с LibGDX, клавиатурой, графикой и источниками уровней.
 *       GameDesktopLauncher и GameApplication запускают игру и связывают ее части.</li>
 * </ul>
 *
 * <h2>Где порты и адаптеры</h2>
 * <ul>
 *   <li>LevelSource — порт получения уровня. TextLevelSource читает текстовое описание,
 *       RandomLevelSource создает случайный уровень.</li>
 *   <li>FieldListener — порт уведомлений поля. FieldRenderer получает эти уведомления
 *       и обновляет список объектов для отрисовки.</li>
 *   <li>InputHandler — порт получения команд. Его реализуют TankController
 *       и лямбда переключения полос здоровья в GameApplication.</li>
 *   <li>ControlStrategy задает способ выбора команды. KeyboardControlStrategy
 *       подключает клавиатуру, а RandomControlStrategy содержит логику бота.</li>
 *   <li>Command задает общий способ выполнить действие. Через него игровой цикл
 *       запускает команды движения, стрельбы и переключения полос здоровья.</li>
 *   <li>renderFrame в GameLoop — функция отрисовки, переданная снаружи.
 *       Она вызывает GameApplication.renderField, а тот обращается к FieldRenderer.</li>
 * </ul>
 * <p>Внутри модели тоже есть свои интерфейсы. Через GameWorld и MovementSpace объекты
 * работают с полем. TankActions, Movable и Shootable описывают действия танка.
 * TankState задает его поведение в разных состояниях, а Tank.StateContext связывает
 * состояния с танком через TankStateContext. ObjectRenderer отвечает за отрисовку
 * отдельного объекта внутри графического адаптера.
 *
 * <h2>Как проходит кадр</h2>
 * <p>GameApplication передает время кадра в GameLoop. Сначала цикл собирает команды
 * игрока и ботов, затем выполняет их и обновляет поле. После этого рисуется результат.
 * Если на поле появился или исчез объект, Field сообщает об этом через FieldListener.
 * Так графика узнает, какие объекты нужно показывать.
 */
package ru.mipt.bit.platformer;
