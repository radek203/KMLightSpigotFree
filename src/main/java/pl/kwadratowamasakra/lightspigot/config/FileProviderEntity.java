package pl.kwadratowamasakra.lightspigot.config;

import java.io.IOException;

/**
 * Interface for entities that provide file configurations.
 * This interface is used to define the methods required for loading and managing files
 */
public interface FileProviderEntity {

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

    /**
     * Creates a configuration file for entity if it does not exist, and loads it.
     * The configuration file is named "config.yml".
     *
     * @return The loaded configuration, or null if an IOException occurred.
     */
    default Configuration createConfig(final FileHelper fileHelper) {
        try {
            return ConfigurationProvider.getYamlProvider().load(fileHelper.getFile("config.yml"));
        } catch (final IOException e) {
            throw new RuntimeException("An error occurred while loading the configuration file.", e);
        }
    }

}
