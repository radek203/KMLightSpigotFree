package pl.kwadratowamasakra.lightspigot.config;

import java.io.IOException;

public class ConfigHelper extends FileHelper {

    /**
     * Constructor for ConfigHelper.
     *
     * @param config The configuration object.
     * @param plugin Boolean indicating if the configuration is for a plugin or server instance.
     */
    public ConfigHelper(final Config config, final boolean plugin) {
        super(config, plugin);
    }

    /**
     * Creates a configuration file if it does not exist, and loads it.
     * The configuration file is named "config.yml".
     *
     * @return The loaded configuration, or null if an IOException occurred.
     */
    public final Configuration createConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile("config.yml"));
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
