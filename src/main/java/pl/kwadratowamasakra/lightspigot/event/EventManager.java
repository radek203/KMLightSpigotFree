package pl.kwadratowamasakra.lightspigot.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The EventManager class manages the events in the system.
 * It provides methods to handle an event and add an event listener for a specific event type.
 */
public class EventManager {

    /**
     * A map that associates each event type with a list of event listeners.
     */
    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> eventListenerMap = new ConcurrentHashMap<>();

    /**
     * Handles an event by calling all the event listeners associated with the event's type.
     *
     * @param event The event to be handled.
     */
    public final void handleEvent(final Event event) {
        final List<EventListener<? extends Event>> eventListenerList = eventListenerMap.get(event.getClass());
        if (eventListenerList != null) {
            for (final EventListener eventListener : eventListenerList) {
                eventListener.handle(event);
            }
        }
    }

    /**
     * Adds an event listener for a specific event type.
     * If there are no event listeners for the event type, a new list is created.
     *
     * @param eventClass    The class of the event type.
     * @param eventListener The event listener to be added.
     */
    public final void addEvent(final Class<? extends Event> eventClass, final EventListener<? extends Event> eventListener) {
        eventListenerMap.putIfAbsent(eventClass, new CopyOnWriteArrayList<>());
        eventListenerMap.get(eventClass).add(eventListener);
    }
}
