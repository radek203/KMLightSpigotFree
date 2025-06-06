package pl.kwadratowamasakra.lightspigot.config;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginFileHelper extends FileHelper {

    public PluginFileHelper(final FileProviderEntity fileProviderEntity) {
        super(fileProviderEntity);
    }

    /**
     * Creates a configuration file if it does not exist, and loads it.
     * If the configuration is for a plugin, the directory is created inside the "plugins" directory.
     *
     * @return The loaded data folder.
     */
    File getFolder() {
        return new File("plugins/" + fileProviderEntity.getName());
    }

    /**
     * Saves a file from the JAR file to the specified end file.
     * The file is read from the JAR file and written to the end file.
     *
     * @param fileName The name of the file in the JAR file.
     * @param endFile  The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    void saveFile(final String fileName, final File endFile) throws IOException {
        final JarFile jarFile = new JarFile(fileProviderEntity.getJarPath());
        final JarEntry entry = jarFile.getJarEntry(fileName);
        if (entry != null) {
            final InputStream is = jarFile.getInputStream(entry);
            final OutputStream os = new FileOutputStream(endFile);
            is.transferTo(os);
            is.close();
            os.close();
        }
    }

}
