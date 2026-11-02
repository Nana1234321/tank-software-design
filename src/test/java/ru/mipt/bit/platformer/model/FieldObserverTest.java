package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldObserverTest {
    private static final class RecordingListener implements FieldListener {
        private final Field field;
        private final Set<GameObject> visible = new LinkedHashSet<>();
        private final List<GameObject> added = new ArrayList<>();
        private final List<GameObject> removed = new ArrayList<>();

        private RecordingListener(Field field) {
            this.field = field;
        }

        @Override
        public void onObjectAdded(GameObject object) {
            assertTrue("Notify after registration", field.getObjects().contains(object));
            visible.add(object);
            added.add(object);
        }

        @Override
        public void onObjectRemoved(GameObject object) {
            assertFalse("Notify after removal", field.getObjects().contains(object));
            visible.remove(object);
            removed.add(object);
        }

        private void assertSynchronized() {
            assertEquals(field.getObjects(), new ArrayList<>(visible));
        }
    }

    @Test public void subscriptionDeliversInitialObjectsAndLaterChanges() {
        Field field = new Field(3, 1);
        Tree first = new Tree(new GridPoint2(0, 0));
        field.addObject(first);
        RecordingListener listener = new RecordingListener(field);
        field.addListener(listener);
        assertEquals(Arrays.asList(first), listener.added);
        listener.assertSynchronized();
        Tree second = new Tree(new GridPoint2(1, 0));
        field.addObject(second);
        assertEquals(Arrays.asList(first, second), listener.added);
        field.removeObject(first);
        assertEquals(Arrays.asList(first), listener.removed);
        listener.assertSynchronized();
    }

    @Test public void failedRegistrationsAndRepeatedRemovalsDoNotEmitEvents() {
        Field field = new Field(2, 1);
        RecordingListener listener = new RecordingListener(field);
        field.addListener(listener);
        Tree tree = new Tree(new GridPoint2(0, 0));
        field.addObject(tree);
        assertThrows(IllegalArgumentException.class, () -> field.addObject(tree));
        assertThrows(IllegalArgumentException.class,
                () -> field.addObject(new Tree(new GridPoint2(0, 0))));
        assertThrows(IllegalArgumentException.class,
                () -> field.addObject(new Tree(new GridPoint2(2, 0))));
        field.removeObject(new Tree(new GridPoint2(1, 0)));
        field.removeObject(tree);
        field.removeObject(tree);
        assertEquals(Arrays.asList(tree), listener.added);
        assertEquals(Arrays.asList(tree), listener.removed);
        listener.assertSynchronized();
    }

    @Test public void duplicateSubscriptionDoesNotRepeatInitialOrFutureEvents() {
        Field field = new Field(2, 1);
        Tree first = new Tree(new GridPoint2(0, 0));
        field.addObject(first);
        RecordingListener listener = new RecordingListener(field);
        field.addListener(listener);
        field.addListener(listener);
        Tree second = new Tree(new GridPoint2(1, 0));
        field.addObject(second);
        field.removeObject(first);
        assertEquals(Arrays.asList(first, second), listener.added);
        assertEquals(Arrays.asList(first), listener.removed);
        listener.assertSynchronized();
    }

    @Test public void unsubscribeStopsEventsWithoutAffectingOtherListeners() {
        Field field = new Field(2, 1);
        RecordingListener first = new RecordingListener(field);
        RecordingListener second = new RecordingListener(field);
        field.addListener(first);
        field.addListener(second);
        Tree tree = new Tree(new GridPoint2(0, 0));
        field.addObject(tree);
        field.removeListener(first);
        field.removeListener(first);
        field.removeObject(tree);
        assertEquals(Arrays.asList(tree), first.added);
        assertTrue(first.removed.isEmpty());
        assertEquals(Arrays.asList(tree), second.added);
        assertEquals(Arrays.asList(tree), second.removed);
        second.assertSynchronized();
    }

    @Test public void listenerMayUnsubscribeDuringNotification() {
        Field field = new Field(2, 1);
        int[] notifications = {0};
        field.addListener(new FieldListener() {
            @Override public void onObjectAdded(GameObject object) {
                notifications[0]++;
                field.removeListener(this);
            }
            @Override public void onObjectRemoved(GameObject object) {
                fail("Listener has unsubscribed");
            }
        });
        RecordingListener other = new RecordingListener(field);
        field.addListener(other);
        Tree tree = new Tree(new GridPoint2(0, 0));
        field.addObject(tree);
        field.removeObject(tree);
        field.addObject(new Tree(new GridPoint2(1, 0)));
        assertEquals(1, notifications[0]);
        other.assertSynchronized();
    }

    @Test public void unsubscribeDuringInitialReplayStopsRemainingNotifications() {
        Field field = new Field(2, 1);
        field.addObject(new Tree(new GridPoint2(0, 0)));
        field.addObject(new Tree(new GridPoint2(1, 0)));
        int[] notifications = {0};
        field.addListener(new FieldListener() {
            @Override public void onObjectAdded(GameObject object) {
                notifications[0]++;
                field.removeListener(this);
            }
            @Override public void onObjectRemoved(GameObject object) {
                fail("Listener has unsubscribed");
            }
        });
        assertEquals(1, notifications[0]);
    }

    @Test public void textAndRandomLevelsInitializeThroughAdditionNotifications() {
        LevelSource[] sources = {
                new TextLevelSource("X__\n_T_"),
                new RandomLevelSource(4, 3, 2, 1, new Random(42))
        };
        for (LevelSource source : sources) {
            Field field = source.load(1f).getField();
            RecordingListener listener = new RecordingListener(field);
            field.addListener(listener);
            assertEquals(field.getObjects(), listener.added);
            listener.assertSynchronized();
            GameObject first = field.getObjects().get(0);
            field.removeObject(first);
            listener.assertSynchronized();
        }
    }

    @Test public void bulletCreationAndLethalHitKeepObserverSynchronized() {
        Field field = new Field(6, 1);
        Tank shooter = new Tank(new GridPoint2(0, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(shooter);
        Tank target = new Tank(new GridPoint2(4, 0),
                Direction.LEFT, field, 1f, 20, 25);
        field.addObject(target);
        field.addObject(new Tree(new GridPoint2(5, 0)));
        RecordingListener listener = new RecordingListener(field);
        field.addListener(listener);
        shooter.shoot();
        assertEquals(4, listener.added.size());
        GameObject bullet = listener.added.get(3);
        assertTrue(bullet instanceof Bullet);
        listener.assertSynchronized();
        field.update(1f);
        assertEquals(Arrays.asList(target, bullet), listener.removed);
        listener.assertSynchronized();
    }

    @Test public void pointBlankHitOnlyNotifiesRemovalOfRegisteredObjects() {
        Field field = new Field(2, 1);
        Tank shooter = new Tank(new GridPoint2(0, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(shooter);
        Tank target = new Tank(new GridPoint2(1, 0),
                Direction.LEFT, field, 1f, 0, 25);
        field.addObject(target);
        RecordingListener listener = new RecordingListener(field);
        field.addListener(listener);
        shooter.shoot();
        assertEquals(Arrays.asList(shooter, target), listener.added);
        assertEquals(Arrays.asList(target), listener.removed);
        listener.assertSynchronized();
    }

    @Test public void collidingBulletsBothGenerateRemovalNotifications() {
        Field field = new Field(5, 1);
        Tank left = new Tank(new GridPoint2(0, 0),
                Direction.RIGHT, field, 1f);
        field.addObject(left);
        Tank right = new Tank(new GridPoint2(4, 0),
                Direction.LEFT, field, 1f);
        field.addObject(right);
        RecordingListener listener = new RecordingListener(field);
        field.addListener(listener);
        left.shoot();
        right.shoot();
        field.update(0.1f);
        assertEquals(4, listener.added.size());
        assertEquals(2, listener.removed.size());
        assertTrue(listener.removed.get(0) instanceof Bullet);
        assertTrue(listener.removed.get(1) instanceof Bullet);
        listener.assertSynchronized();
    }
}
