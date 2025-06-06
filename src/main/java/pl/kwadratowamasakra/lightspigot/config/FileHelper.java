package pl.kwadratowamasakra.lightspigot.config;

import java.io.File;
import java.io.IOException;

public abstract class FileHelper {

    protected final FileProviderEntity fileProviderEntity;

    public FileHelper(final FileProviderEntity fileProviderEntity) {
        this.fileProviderEntity = fileProviderEntity;
    }

    /**
     * Creates a configuration file if it does not exist, and loads it.
     * If the configuration is for a plugin, the directory is created inside the "plugins" directory.
     *
     * @return The loaded data folder.
     */
    private File getDataFolder() {
        final File dataFolder = getFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        return dataFolder;
    }

    abstract File getFolder();

    /**
     * Creates a specific file if it does not exist, and loads it.
     * If the specific file does not exist, it is created.
     * If the specific is for a plugin, the specific file is saved directly from the JAR file.
     * Otherwise, it is saved from the resources.
     *
     * @return The loaded file, or null if an IOException occurred.
     */
    public final File getFile(final String fileName) throws IOException {
        final File file = new File(getDataFolder(), fileName);
        if (!file.exists()) {
            file.createNewFile();
            saveFile(fileName, file);
        }
        return file;
    }

    abstract void saveFile(final String fileName, final File endFile) throws IOException;

}
