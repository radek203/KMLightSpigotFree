package pl.kwadratowamasakra.lightspigot.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This class is a modified version of the YamlConfiguration class from the BungeeCord project (https://github.com/SpigotMC/BungeeCord/blob/master/config/src/main/java/net/md_5/bungee/config/)
 */
public class YamlConfiguration extends ConfigurationProvider {

    private static final ThreadLocal<Yaml> yaml = ThreadLocal.withInitial(() -> {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        final Representer representer = new Representer(options) {
            {
                representers.put(Configuration.class, data -> represent(((Configuration) data).getSelf()));
            }
        };

        return new Yaml(new Constructor(new LoaderOptions()), representer, options);
    });

    @Override
    public final void save(final Configuration config, final File file) throws IOException {
        try (final Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            save(config, writer);
        }
    }

    @Override
    public final void save(final Configuration config, final Writer writer) {
        yaml.get().dump(config.getSelf(), writer);
    }

    @Override
    public final Configuration load(final File file) throws IOException {
        return load(file, null);
    }

    @Override
    public final Configuration load(final File file, final Configuration defaults) throws IOException {
        try (final FileInputStream is = new FileInputStream(file)) {
            return load(is, defaults);
        }
    }

    @Override
    public final Configuration load(final Reader reader) {
        return load(reader, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Configuration load(final Reader reader, final Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public final Configuration load(final InputStream is) {
        return load(is, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Configuration load(final InputStream is, final Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public final Configuration load(final String s) {
        return load(s, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Configuration load(final String s, final Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(s, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }
}
