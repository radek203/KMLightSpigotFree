package pl.kwadratowamasakra.lightspigot.config;

import java.io.*;

public class ServerFileHelper extends FileHelper {

    public ServerFileHelper(final FileProviderEntity fileProviderEntity) {
        super(fileProviderEntity);
    }

    /**
     * Creates a configuration file if it does not exist, and loads it.
     * If the configuration is for a plugin, the directory is created inside the "plugins" directory.
     *
     * @return The loaded data folder.
     */
    File getFolder() {
        return new File(fileProviderEntity.getName());
    }

    /**
     * Saves a file from the resources to the specified end file.
     * The file is read from the resources and written to the end file.
     *
     * @param fileName The name of the file in the resources.
     * @param endFile  The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    void saveFile(final String fileName, final File endFile) throws IOException {
        final InputStream is = fileProviderEntity.getClassLoader().getResourceAsStream(fileName);
        final OutputStream os = new FileOutputStream(endFile);
        is.transferTo(os);
        is.close();
        os.close();
    }

}
