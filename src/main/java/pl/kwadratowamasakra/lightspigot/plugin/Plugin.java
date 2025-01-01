package pl.kwadratowamasakra.lightspigot.plugin;

import pl.kwadratowamasakra.lightspigot.config.Config;
import pl.kwadratowamasakra.lightspigot.config.ConfigHelper;
import pl.kwadratowamasakra.lightspigot.config.Configuration;
import pl.kwadratowamasakra.lightspigot.config.FileHelper;

import java.lang.reflect.Method;

/**
 * The Plugin class represents a plugin in the system.
 * It includes methods to get the enable and disable methods, the instance of the plugin, and the configuration of the plugin.
 */
public class Plugin implements Config {

    private final String name;
    private final String jarPath;
    private final Method methodEnable;
    private final Method methodDisable;
    private final Object instance;
    private final Configuration config;
    private final FileHelper fileHelper;

    /**
     * Constructs a new Plugin with the specified enable and disable methods, and the instance of the plugin.
     * The configuration of the plugin is created using the ConfigHelper.
     *
     * @param name          The name of the plugin.
     * @param methodEnable  The enable method of the plugin.
     * @param methodDisable The disable method of the plugin.
     * @param instance      The instance of the plugin.
     */
    public Plugin(final String name, final String jarPath, final Method methodEnable, final Method methodDisable, final Object instance) {
        this.name = name;
        this.jarPath = jarPath;
        this.methodEnable = methodEnable;
        this.methodDisable = methodDisable;
        this.instance = instance;

        config = new ConfigHelper(this, true).createConfig();
        fileHelper = new FileHelper(this, true);
    }

    /**
     * @return The enable method of this plugin.
     */
    public final Method getMethodEnable() {
        return methodEnable;
    }

    /**
     * @return The disable method of this plugin.
     */
    public final Method getMethodDisable() {
        return methodDisable;
    }

    /**
     * @return The instance of this plugin.
     */
    public final Object getInstance() {
        return instance;
    }

    /**
     * @return The configuration of this plugin.
     */
    public final Configuration getConfig() {
        return config;
    }

    /**
     * @return The file helper of this plugin.
     */
    public final FileHelper getFileHelper() {
        return fileHelper;
    }

    /**
     * Returns the ClassLoader of this plugin.
     * This can be used to load resources related to the plugin.
     * But it is not recommended to use this method due to files shadowing issues.
     *
     * @return The ClassLoader of this plugin.
     */
    @Override
    public final ClassLoader getClassLoader() {
        return instance.getClass().getClassLoader();
    }

    /**
     * @return The name of this plugin.
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * @return The path of the plugin jar file.
     */
    @Override
    public final String getJarPath() {
        return jarPath;
    }
}
