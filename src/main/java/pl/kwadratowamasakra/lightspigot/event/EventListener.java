package pl.kwadratowamasakra.lightspigot.event;

/**
 * The EventListener interface provides a contract for event listener classes.
 * It provides a method to handle the event.
 * Plugin developers can implement this interface to create event listeners.
 *
 * @param <T> The type of the event to handle.
 */
@FunctionalInterface
public interface EventListener<T> {

    /**
     * Handles the event.
     * This method is called when the event occurs.
     *
     * @param e The event to handle.
     */
    void handle(final T e);

}
