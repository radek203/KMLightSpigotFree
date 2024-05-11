package pl.kwadratowamasakra.lightspigot.config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/*
 * This class is a modified version of the ConfigurationProvider class from the BungeeCord project (https://github.com/SpigotMC/BungeeCord/blob/master/config/src/main/java/net/md_5/bungee/config/)
 */
public abstract class ConfigurationProvider {

    private static final Map<Class<? extends ConfigurationProvider>, ConfigurationProvider> providers = new HashMap<>();

    static {
        try {
            providers.put(YamlConfiguration.class, new YamlConfiguration());
        } catch (final NoClassDefFoundError ex) {
            // Ignore, no SnakeYAML
        }
    }

    public static ConfigurationProvider getProvider(final Class<? extends ConfigurationProvider> provider) {
        return providers.get(provider);
    }

    /*------------------------------------------------------------------------*/
    public abstract void save(Configuration config, File file) throws IOException;

    public abstract void save(Configuration config, Writer writer);

    public abstract Configuration load(File file) throws IOException;

    public abstract Configuration load(File file, Configuration defaults) throws IOException;

    public abstract Configuration load(Reader reader);

    public abstract Configuration load(Reader reader, Configuration defaults);

    public abstract Configuration load(InputStream is);

    public abstract Configuration load(InputStream is, Configuration defaults);

    public abstract Configuration load(String s);

    public abstract Configuration load(String s, Configuration defaults);
}
