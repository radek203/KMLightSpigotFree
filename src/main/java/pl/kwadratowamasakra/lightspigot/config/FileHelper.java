package pl.kwadratowamasakra.lightspigot.config;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileHelper {

    protected final Config config;
    protected final boolean plugin;

    public FileHelper(final Config config, final boolean plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    /**
     * Creates a configuration file if it does not exist, and loads it.
     * If the configuration is for a plugin, the directory is created inside the "plugins" directory.
     *
     * @return The loaded data folder.
     */
    public final File getDataFolder() {
        final File dataFolder = plugin ? new File("plugins/" + config.getName()) : new File(config.getName());
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        return dataFolder;
    }

    /**
     * Saves a file from the JAR file to the specified end file.
     * The file is read from the JAR file and written to the end file.
     *
     * @param fileName The name of the file in the JAR file.
     * @param endFile  The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    public final void saveFromJar(final String fileName, final File endFile) throws IOException {
        final JarFile jarFile = new JarFile(config.getJarPath());
        final JarEntry entry = jarFile.getJarEntry(fileName);
        if (entry !=  null) {
            final InputStream is = jarFile.getInputStream(entry);
            final OutputStream os = new FileOutputStream(endFile);
            is.transferTo(os);
            is.close();
            os.close();
        }
    }

    /**
     * Saves a file from the resources to the specified end file.
     * The file is read from the resources and written to the end file.
     *
     * @param fileName The name of the file in the resources.
     * @param endFile  The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    public final void saveFromResource(final String fileName, final File endFile) throws IOException {
        final InputStream is = config.getClassLoader().getResourceAsStream(fileName);
        final OutputStream os = new FileOutputStream(endFile);
        is.transferTo(os);
        is.close();
        os.close();
    }

    /**
     * Creates a specific file if it does not exist, and loads it.
     * If the specific file does not exist, it is created.
     * If the specific is for a plugin, the specific file is saved directly from the JAR file.
     * Otherwise, it is saved from the resources.
     *
     * @return The loaded file, or null if an IOException occurred.
     */
    public final File getFile(final String fileName) {
        try {
            final File file = new File(getDataFolder(), fileName);
            if (!file.exists()) {
                file.createNewFile();
                if (plugin) {
                    saveFromJar(fileName, file);
                } else {
                    saveFromResource(fileName, file);
                }
            }
            return file;
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
