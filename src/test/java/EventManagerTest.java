import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.event.Event;
import pl.kwadratowamasakra.lightspigot.event.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class EventManagerTest {

    @Test
    void listenersShouldRunInRegistrationOrderForExactEventType() {
        final EventManager manager = new EventManager();
        final List<Integer> calls = new ArrayList<>();
        manager.addEvent(TestEvent.class, event -> calls.add(1));
        manager.addEvent(TestEvent.class, event -> calls.add(2));

        manager.handleEvent(new TestEvent());

        Assertions.assertEquals(List.of(1, 2), calls);
    }

    @Test
    void unrelatedEventTypesShouldNotReceiveEvents() {
        final EventManager manager = new EventManager();
        final AtomicInteger calls = new AtomicInteger();
        manager.addEvent(TestEvent.class, event -> calls.incrementAndGet());

        manager.handleEvent(new OtherEvent());

        Assertions.assertEquals(0, calls.get());
    }

    @Test
    void eventWithoutListenersShouldBeSafe() {
        final EventManager manager = new EventManager();
        Assertions.assertDoesNotThrow(() -> manager.handleEvent(new TestEvent()));
    }

    private static class TestEvent extends Event {
    }

    private static class OtherEvent extends Event {
    }
}
