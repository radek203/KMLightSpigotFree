package pl.kwadratowamasakra.lightspigot.plugin;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The PluginRunnable class represents a task that can be scheduled to run at fixed intervals.
 * It includes methods to cancel the task and remove it from the server's plugin manager.
 */
public class PluginRunnable {

    private final LightSpigotServer server;
    private final Timer cooldownTimer;

    /**
     * Constructs a new PluginRunnable with the specified server, task, delay, and period.
     * The task is scheduled to run after the specified delay, and then at fixed intervals.
     *
     * @param server        The server that the task is associated with.
     * @param timerTaskInfo The task to be scheduled.
     * @param delay         The delay in milliseconds before the task is to be executed.
     * @param period        The time in milliseconds between successive task executions.
     */
    public PluginRunnable(final LightSpigotServer server, final TimerTask timerTaskInfo, final long delay, final long period) {
        this.server = server;

        cooldownTimer = new Timer();
        cooldownTimer.schedule(timerTaskInfo, delay, period);

        server.getPluginManager().addPluginRunnable(this);
    }

    /**
     * Constructs a new PluginRunnable with the specified server, task, and delay.
     * The task is scheduled to run once after the specified delay.
     *
     * @param server        The server that the task is associated with.
     * @param timerTaskInfo The task to be scheduled.
     * @param delay         The delay in milliseconds before the task is to be executed.
     */
    public PluginRunnable(final LightSpigotServer server, final TimerTask timerTaskInfo, final long delay) {
        this.server = server;

        cooldownTimer = new Timer();
        cooldownTimer.schedule(timerTaskInfo, delay);

        server.getPluginManager().addPluginRunnable(this);
    }

    /**
     * Cancels the task.
     * This stops the task from running in the future.
     */
    public final void cancelTask() {
        cooldownTimer.cancel();
    }

    /**
     * Cancels the task and removes it from the server's plugin manager.
     * This stops the task from running in the future and removes it from the list of plugin runnables.
     */
    public final void cancel() {
        cooldownTimer.cancel();
        server.getPluginManager().removePluginRunnable(this);
    }

}
