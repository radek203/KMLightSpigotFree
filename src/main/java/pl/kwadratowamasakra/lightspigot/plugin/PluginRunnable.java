package pl.kwadratowamasakra.lightspigot.plugin;

import lombok.Getter;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;

import java.util.concurrent.Future;

/**
 * The PluginRunnable class represents a task that can be scheduled to run at fixed intervals.
 * It includes methods to cancel the task and remove it from the server's plugin manager.
 */
public class PluginRunnable {

    private final LightSpigotServer server;
    @Getter
    private final Runnable runnable;
    private final Future<?> scheduledFuture;

    /**
     * Constructs a new PluginRunnable with the specified server, task, delay, and period.
     * The task is scheduled to run after the specified delay, and then at fixed intervals.
     *
     * @param server   The server that the task is associated with.
     * @param runnable The task to be scheduled.
     * @param delay    The delay in milliseconds before the task is to be executed.
     * @param period   The time in milliseconds between successive task executions.
     */
    public PluginRunnable(final LightSpigotServer server, final Runnable runnable, final long delay, final long period) {
        this.server = server;
        this.runnable = runnable;

        scheduledFuture = server.getPluginManager().schedulePluginRunnable(this, delay, period);
    }

    /**
     * Constructs a new PluginRunnable with the specified server, task, and delay.
     * The task is scheduled to run once after the specified delay.
     *
     * @param server   The server that the task is associated with.
     * @param runnable The task to be scheduled.
     * @param delay    The delay in milliseconds before the task is to be executed.
     */
    public PluginRunnable(final LightSpigotServer server, final Runnable runnable, final long delay) {
        this.server = server;
        this.runnable = () -> {
            runnable.run();
            removeTask();
        };

        scheduledFuture = server.getPluginManager().schedulePluginRunnable(this, delay);
    }

    /**
     * Constructs a new PluginRunnable with the specified server and task.
     * The task is scheduled to run once immediately.
     *
     * @param server   The server that the task is associated with.
     * @param runnable The task to be scheduled.
     */
    public PluginRunnable(final LightSpigotServer server, final Runnable runnable) {
        this.server = server;
        this.runnable = () -> {
            runnable.run();
            removeTask();
        };

        scheduledFuture = server.getPluginManager().schedulePluginRunnable(this);
    }

    /**
     * Cancels the task.
     * This stops the task from running in the future.
     */
    public final void cancelTask() {
        scheduledFuture.cancel(true);
    }

    /**
     * Cancels the task and removes it from the server's plugin manager.
     * This stops the task from running in the future and removes it from the list of plugin runnables.
     */
    public final void cancel() {
        cancelTask();
        removeTask();
    }

    /**
     * Removes the task from the server's plugin manager.
     * This removes the task from the list of plugin runnables.
     */
    private void removeTask() {
        server.getPluginManager().removePluginRunnable(this);
    }

}
