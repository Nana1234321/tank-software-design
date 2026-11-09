package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import ru.mipt.bit.platformer.command.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import static org.junit.Assert.*;

public class HealthBarRendererTest {
    private static final class RecordingBatch {
        final List<float[]> draws = new ArrayList<>();
        final Color color = new Color(0.2f, 0.4f, 0.6f, 0.8f);
        boolean failOnDraw;
        final Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[]{Batch.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getPackedColor")) return color.toFloatBits();
                    if (method.getName().equals("setPackedColor")) {
                        Color.abgr8888ToColor(color, (Float) args[0]);
                    }
                    if (method.getName().equals("setColor")) color.set((Color) args[0]);
                    if (method.getName().equals("draw")) {
                        if (failOnDraw) throw new IllegalStateException("Drawing failed");
                        draws.add(new float[]{(Float) args[1], (Float) args[2], (Float) args[3]});
                    }
                    return null;
                });
    }

    private static void renderFrame(ObjectRenderer renderer, Batch batch, GameObject... objects) {
        for (GameObject object : objects) renderer.render(batch, object, 100, 100);
        for (GameObject object : objects) renderer.renderOverlay(batch, object, 100, 100);
    }

    @Test public void commandTogglesBarsForAllTanksAndPreservesAppearance() {
        HealthBarVisibility visibility = new HealthBarVisibility();
        ToggleHealthBarsCommand command = new ToggleHealthBarsCommand(visibility);
        RecordingBatch recording = new RecordingBatch();
        List<float[]> draws = recording.draws;
        Batch batch = recording.batch;
        float originalColor = recording.color.toFloatBits();
        int[] appearances = {0};
        ObjectRenderer renderer = new HealthBarRendererDecorator(
                (b, object, w, h) -> appearances[0]++, visibility, new TextureRegion());
        Field field = new Field(3, 1);
        Tank player = new Tank(new GridPoint2(0, 0), Direction.RIGHT, field, 1f);
        Tank enemy = new Tank(new GridPoint2(1, 0), Direction.RIGHT, field, 1f);
        field.addObject(player);
        field.addObject(enemy);
        enemy.takeDamage(75);

        renderFrame(renderer, batch, player);
        assertTrue(draws.isEmpty());
        command.execute();
        assertTrue(visibility.isVisible());
        renderFrame(renderer, batch, player, enemy);
        assertEquals(4, draws.size());
        assertEquals(70f, draws.get(1)[2], 0.001f);
        assertEquals(17.5f, draws.get(3)[2], 0.001f);
        assertEquals(originalColor, recording.color.toFloatBits(), 0f);

        enemy.move(Direction.RIGHT);
        enemy.update(0.5f);
        renderFrame(renderer, batch, enemy);
        assertTrue(draws.get(4)[0] > draws.get(2)[0]);
        renderFrame(renderer, batch, new Tree(new GridPoint2(2, 0)));
        assertEquals(6, draws.size());
        command.execute();
        assertFalse(visibility.isVisible());
        renderFrame(renderer, batch, player);
        assertEquals(6, draws.size());
        assertEquals(6, appearances[0]);
        assertEquals(100, enemy.getMaxHealth());
    }

    @Test public void barsAreDrawnOnlyAfterAllAppearances() {
        HealthBarVisibility visibility = new HealthBarVisibility();
        visibility.toggle();
        RecordingBatch recording = new RecordingBatch();
        int[] appearances = {0};
        ObjectRenderer renderer = new HealthBarRendererDecorator((batch, object, w, h) -> {
            assertTrue(recording.draws.isEmpty());
            appearances[0]++;
        }, visibility, new TextureRegion());
        Field field = new Field(2, 1);
        Tank tank = new Tank(new GridPoint2(), Direction.RIGHT, field, 1f);
        Tree tree = new Tree(new GridPoint2(1, 0));
        renderFrame(renderer, recording.batch, tank, tree);
        assertEquals(2, appearances[0]);
        assertEquals(2, recording.draws.size());
    }

    @Test public void customAndZeroHealthProduceFiniteBarsAndDeadTanksHaveNoBars() {
        HealthBarVisibility visibility = new HealthBarVisibility();
        visibility.toggle();
        RecordingBatch recording = new RecordingBatch();
        ObjectRenderer renderer = new HealthBarRendererDecorator((b, object, w, h) -> {},
                visibility, new TextureRegion());
        Field field = new Field(2, 1);
        Tank custom = new Tank(new GridPoint2(), Direction.RIGHT, field, 1f, 40, 25);
        custom.takeDamage(10);
        renderFrame(renderer, recording.batch, custom);
        assertEquals(52.5f, recording.draws.get(1)[2], 0.001f);
        custom.takeDamage(30);
        assertTrue(custom.isAlive());
        renderFrame(renderer, recording.batch, custom);
        assertEquals(3, recording.draws.size());
        assertEquals(70f, recording.draws.get(2)[2], 0.001f);
        Tank zero = new Tank(new GridPoint2(1, 0), Direction.RIGHT, field, 1f, 0, 25);
        renderFrame(renderer, recording.batch, zero);
        assertEquals(4, recording.draws.size());
        assertEquals(70f, recording.draws.get(3)[2], 0.001f);
        custom.takeDamage(1);
        assertFalse(custom.isAlive());
        renderFrame(renderer, recording.batch, custom);
        assertEquals(4, recording.draws.size());
    }

    @Test public void batchColorIsRestoredEvenWhenDrawingFails() {
        HealthBarVisibility visibility = new HealthBarVisibility();
        visibility.toggle();
        RecordingBatch recording = new RecordingBatch();
        recording.failOnDraw = true;
        float originalColor = recording.color.toFloatBits();
        ObjectRenderer renderer = new HealthBarRendererDecorator((b, object, w, h) -> {},
                visibility, new TextureRegion());
        Tank tank = new Tank(new GridPoint2(), Direction.RIGHT, new Field(1, 1), 1f);
        assertThrows(IllegalStateException.class, () -> renderFrame(renderer, recording.batch, tank));
        assertEquals(originalColor, recording.color.toFloatBits(), 0f);
    }
}
