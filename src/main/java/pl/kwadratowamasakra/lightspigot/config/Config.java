package pl.kwadratowamasakra.lightspigot.config;

/**
 * The Config interface provides a contract for configuration classes.
 * It provides methods to get the ClassLoader and the name of the configuration.
 */
public interface Config {

    /**
     * Gets the ClassLoader of the configuration.
     * This can be used to load resources related to the configuration.
     *
     * @return the ClassLoader of the configuration.
     */
    ClassLoader getClassLoader();

    /**
     * Gets the name of the configuration. (e.g. "LightSpigotServer" or plugin name)
     *
     * @return the name of the configuration.
     */
    String getName();

    /**
     * @return the path of the jar file.
     */
    String getJarPath();

}
