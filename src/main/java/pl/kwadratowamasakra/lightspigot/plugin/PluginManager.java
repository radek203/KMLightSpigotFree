package pl.kwadratowamasakra.lightspigot.plugin;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.command.StopCommand;
import pl.kwadratowamasakra.lightspigot.config.Configuration;
import pl.kwadratowamasakra.lightspigot.config.FileHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.jar.JarFile;

/**
 * The PluginManager class manages the plugins in the system.
 * It provides methods to load and disable plugins, and manage plugin runnables.
 */
public class PluginManager {

    /**
     * A list of plugins.
     */
    private final List<Plugin> plugins = new ArrayList<>();

    /**
     * A list of plugin runnables.
     */
    private final List<PluginRunnable> runnables = new ArrayList<>();

    /**
     * An executor for the plugin runnables.
     */
    private final ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    /**
     * A scheduler for the plugin runnables.
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Checks if a class has a specific method.
     *
     * @param main       The class to check.
     * @param methodName The name of the method to check for.
     * @return true if the class has the method, false otherwise.
     */
    private static boolean hasMethod(final Class<?> main, final String methodName) {
        boolean hasMethod = false;
        final Method[] methods = main.getMethods();
        for (final Method method : methods) {
            if (method.getName().equals(methodName)) {
                hasMethod = true;
                break;
            }
        }
        return hasMethod;
    }

    /**
     * Loads the plugins for the server from the 'plugins' directory.
     *
     * @param server The server to load the plugins for.
     */
    public final void loadPlugins(final LightSpigotServer server) {
        new StopCommand(server);

        try {
            final File file = new File("plugins");
            if (!file.exists()) {
                file.mkdir();
            }

            for (final String s : file.list()) {
                if (s.endsWith(".jar")) {
                    final File pl = new File("plugins/" + s);

                    final JarFile jf = new JarFile(pl);
                    final String mainClassName = jf.getManifest().getMainAttributes().getValue("Main-Class");
                    final String pluginName = jf.getManifest().getMainAttributes().getValue("Plugin-Name");

                    final URLClassLoader child = new URLClassLoader(new URL[]{pl.toURI().toURL()}, getClass().getClassLoader());
                    final Class<?> classToLoad = Class.forName(mainClassName, true, child);
                    final Method methodEnable = classToLoad.getDeclaredMethod("onEnable", LightSpigotServer.class, Configuration.class, FileHelper.class);
                    final Method methodDisable = hasMethod(classToLoad, "onDisable") ? classToLoad.getDeclaredMethod("onDisable") : null;
                    final Object instance = classToLoad.getConstructor().newInstance();

                    final Plugin plugin = new Plugin(pluginName, pl.getPath(), methodEnable, methodDisable, instance);
                    plugins.add(plugin);
                    methodEnable.invoke(instance, server, plugin.getConfig(), plugin.getFileHelper());
                    jf.close();

                    server.getLogger().info(" [PLUGIN] " + pluginName + " has been loaded successfully!");
                }
            }
        } catch (final IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Disables all the plugins.
     */
    public final void disablePlugins() {
        stopPluginRunnables();

        try {
            for (final Plugin plugin : plugins) {
                if (plugin.getMethodDisable() != null) {
                    plugin.getMethodDisable().invoke(plugin.getInstance());
                }
            }
        } catch (final IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    final Future<?> schedulePluginRunnable(final Runnable runnable, final long delay, final long period) {
        return scheduler.scheduleAtFixedRate(() -> executor.execute(runnable), delay, period, TimeUnit.MILLISECONDS);
    }

    final Future<?> schedulePluginRunnable(final Runnable runnable, final long delay) {
        return scheduler.schedule(() -> executor.execute(runnable), delay, TimeUnit.MILLISECONDS);
    }

    final Future<?> schedulePluginRunnable(final Runnable runnable) {
        return executor.submit(runnable);
    }

    /**
     * Adds a plugin runnable.
     *
     * @param runnable The plugin runnable to add.
     */
    final void addPluginRunnable(final PluginRunnable runnable) {
        runnables.add(runnable);
    }

    /**
     * Removes a plugin runnable.
     *
     * @param runnable The plugin runnable to remove.
     */
    final void removePluginRunnable(final PluginRunnable runnable) {
        runnables.remove(runnable);
    }

    /**
     * Stops all the plugin runnables.
     */
    private void stopPluginRunnables() {
        for (final PluginRunnable runnable : runnables) {
            runnable.cancelTask();
        }
        runnables.clear();
    }

}
