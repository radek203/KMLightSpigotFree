package pl.kwadratowamasakra.lightspigot.config;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ConfigHelper {

    private final Config config;
    private final boolean plugin;

    /**
     * Constructor for ConfigHelper.
     *
     * @param config The configuration object.
     * @param plugin Boolean indicating if the configuration is for a plugin or server instance.
     */
    public ConfigHelper(final Config config, final boolean plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    /**
     * Creates a configuration file if it does not exist, and loads it.
     * The configuration file is created in a directory named after the configuration's name.
     * If the configuration is for a plugin, the directory is created inside the "plugins" directory.
     * The configuration file is named "config.yml".
     * If the configuration file does not exist, it is created.
     * If the configuration is for a plugin, the configuration file is saved directly from the JAR file.
     * Otherwise, it is saved from the resources.
     *
     * @return The loaded configuration, or null if an IOException occurred.
     */
    public final Configuration createConfig() {
        try {
            final File dataFolder = plugin ? new File("plugins/" + config.getName()) : new File(config.getName());
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            final File configFile = new File(dataFolder, "config.yml");
            if (!configFile.exists()) {
                configFile.createNewFile();
                if (plugin) {
                    saveFromJar("config.yml", configFile);
                } else {
                    saveFromResource("config.yml", configFile);
                }
            }
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves a file from the JAR file to the specified end file.
     * The file is read from the JAR file and written to the end file.
     *
     * @param fileName The name of the file in the JAR file.
     * @param endFile  The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    private void saveFromJar(final String fileName, final File endFile) throws IOException {
        final JarFile jarFile = new JarFile(config.getJarPath());
        final JarEntry entry = jarFile.getJarEntry(fileName);
        final InputStream is = jarFile.getInputStream(entry);
        final OutputStream os = new FileOutputStream(endFile);
        is.transferTo(os);
        is.close();
        os.close();
    }

    /**
     * Saves a file from the resources to the specified end file.
     * The file is read from the resources and written to the end file.
     *
     * @param fileName The name of the file in the resources.
     * @param endFile  The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    private void saveFromResource(final String fileName, final File endFile) throws IOException {
        final InputStream is = config.getClassLoader().getResourceAsStream(fileName);
        final OutputStream os = new FileOutputStream(endFile);
        is.transferTo(os);
        is.close();
        os.close();
    }

}
