package pl.kwadratowamasakra.lightspigot.config;

import java.util.*;

/*
 * This class is a modified version of the Configuration class from the BungeeCord project (https://github.com/SpigotMC/BungeeCord/blob/master/config/src/main/java/net/md_5/bungee/config/)
 */
public class Configuration {

    private static final char SEPARATOR = '.';
    private final Map<String, Object> self;
    private final Configuration defaults;

    public Configuration() {
        this(null);
    }

    public Configuration(final Configuration defaults) {
        this(new LinkedHashMap<String, Object>(), defaults);
    }

    Configuration(final Map<?, ?> map, final Configuration defaults) {
        self = new LinkedHashMap<>();
        this.defaults = defaults;

        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            final String key = (entry.getKey() == null) ? "null" : entry.getKey().toString();

            if (entry.getValue() instanceof Map) {
                self.put(key, new Configuration((Map<?, ?>) entry.getValue(), (defaults == null) ? null : defaults.getSection(key)));
            } else {
                self.put(key, entry.getValue());
            }
        }
    }

    public final Map<String, Object> getSelf() {
        return self;
    }


    private Configuration getSectionFor(final String path) {
        final int index = path.indexOf(SEPARATOR);
        if (index == -1) {
            return this;
        }

        final String root = path.substring(0, index);
        Object section = self.get(root);
        if (section == null) {
            section = new Configuration((defaults == null) ? null : defaults.getSection(root));
            self.put(root, section);
        }

        return (Configuration) section;
    }

    private String getChild(final String path) {
        final int index = path.indexOf(SEPARATOR);
        return (index == -1) ? path : path.substring(index + 1);
    }

    /*------------------------------------------------------------------------*/
    @SuppressWarnings("unchecked")
    public final <T> T get(final String path, final T def) {
        final Configuration section = getSectionFor(path);
        final Object val;
        if (section == this) {
            val = self.get(path);
        } else {
            val = section.get(getChild(path), def);
        }

        if (val == null && def instanceof Configuration) {
            self.put(path, def);
        }

        return (val != null) ? (T) val : def;
    }

    public final boolean contains(final String path) {
        return get(path, null) != null;
    }

    public final Object get(final String path) {
        return get(path, getDefault(path));
    }

    public final Object getDefault(final String path) {
        return (defaults == null) ? null : defaults.get(path);
    }

    public final void set(final String path, final Object value) {
        Object o = value;
        if (o instanceof Map) {
            o = new Configuration((Map<?, ?>) o, (defaults == null) ? null : defaults.getSection(path));
        }

        final Configuration section = getSectionFor(path);
        if (section == this) {
            if (o == null) {
                self.remove(path);
            } else {
                self.put(path, o);
            }
        } else {
            section.set(getChild(path), o);
        }
    }

    /*------------------------------------------------------------------------*/
    public final Configuration getSection(final String path) {
        final Object def = getDefault(path);
        return (Configuration) get(path, (def instanceof Configuration) ? def : new Configuration((defaults == null) ? null : defaults.getSection(path)));
    }

    /**
     * Gets keys, not deep by default.
     *
     * @return top level keys for this section
     */
    public final Collection<String> getKeys() {
        return new LinkedHashSet<>(self.keySet());
    }

    /*------------------------------------------------------------------------*/
    public final byte getByte(final String path) {
        final Object def = getDefault(path);
        return getByte(path, (def instanceof Number) ? ((Number) def).byteValue() : 0);
    }

    public final byte getByte(final String path, final byte def) {
        final Number val = get(path, def);
        return (val != null) ? val.byteValue() : def;
    }

    public final List<Byte> getByteList(final String path) {
        final List<?> list = getList(path);
        final List<Byte> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }

        return result;
    }

    public final short getShort(final String path) {
        final Object def = getDefault(path);
        return getShort(path, (def instanceof Number) ? ((Number) def).shortValue() : 0);
    }

    public final short getShort(final String path, final short def) {
        final Number val = get(path, def);
        return (val != null) ? val.shortValue() : def;
    }

    public final List<Short> getShortList(final String path) {
        final List<?> list = getList(path);
        final List<Short> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }

        return result;
    }

    public final int getInt(final String path) {
        final Object def = getDefault(path);
        return getInt(path, (def instanceof Number) ? ((Number) def).intValue() : 0);
    }

    public final int getInt(final String path, final int def) {
        final Number val = get(path, def);
        return (val != null) ? val.intValue() : def;
    }

    public final List<Integer> getIntList(final String path) {
        final List<?> list = getList(path);
        final List<Integer> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }

        return result;
    }

    public final long getLong(final String path) {
        final Object def = getDefault(path);
        return getLong(path, (def instanceof Number) ? ((Number) def).longValue() : 0);
    }

    public final long getLong(final String path, final long def) {
        final Number val = get(path, def);
        return (val != null) ? val.longValue() : def;
    }

    public final List<Long> getLongList(final String path) {
        final List<?> list = getList(path);
        final List<Long> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }

        return result;
    }

    public final float getFloat(final String path) {
        final Object def = getDefault(path);
        return getFloat(path, (def instanceof Number) ? ((Number) def).floatValue() : 0);
    }

    public final float getFloat(final String path, final float def) {
        final Number val = get(path, def);
        return (val != null) ? val.floatValue() : def;
    }

    public final List<Float> getFloatList(final String path) {
        final List<?> list = getList(path);
        final List<Float> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }

        return result;
    }

    public final double getDouble(final String path) {
        final Object def = getDefault(path);
        return getDouble(path, (def instanceof Number) ? ((Number) def).doubleValue() : 0);
    }

    public final double getDouble(final String path, final double def) {
        final Number val = get(path, def);
        return (val != null) ? val.doubleValue() : def;
    }

    public final List<Double> getDoubleList(final String path) {
        final List<?> list = getList(path);
        final List<Double> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }

        return result;
    }

    public final boolean getBoolean(final String path) {
        final Object def = getDefault(path);
        return getBoolean(path, (def instanceof Boolean) ? (Boolean) def : false);
    }

    public final boolean getBoolean(final String path, final boolean def) {
        final Boolean val = get(path, def);
        return (val != null) ? val : def;
    }

    public final List<Boolean> getBooleanList(final String path) {
        final List<?> list = getList(path);
        final List<Boolean> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            }
        }

        return result;
    }

    public final char getChar(final String path) {
        final Object def = getDefault(path);
        return getChar(path, (def instanceof Character) ? (Character) def : '\u0000');
    }

    public final char getChar(final String path, final char def) {
        final Character val = get(path, def);
        return (val != null) ? val : def;
    }

    public final List<Character> getCharList(final String path) {
        final List<?> list = getList(path);
        final List<Character> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            }
        }

        return result;
    }

    public final String getString(final String path) {
        final Object def = getDefault(path);
        return getString(path, (def instanceof String) ? (String) def : "");
    }

    public final String getString(final String path, final String def) {
        final String val = get(path, def);
        return (val != null) ? val : def;
    }

    public final List<String> getStringList(final String path) {
        final List<?> list = getList(path);
        final List<String> result = new ArrayList<>();

        for (final Object object : list) {
            if (object instanceof String) {
                result.add((String) object);
            }
        }

        return result;
    }

    /*------------------------------------------------------------------------*/
    public final List<?> getList(final String path) {
        final Object def = getDefault(path);
        return getList(path, (def instanceof List<?>) ? (List<?>) def : Collections.emptyList());
    }

    public final List<?> getList(final String path, final List<?> def) {
        final List<?> val = get(path, def);
        return (val != null) ? val : def;
    }
}
