package pl.kwadratowamasakra.lightspigot.world.utils;

import java.util.Map;

public final class MapUtils {

    private MapUtils() {
    }

    public static int readInt(final Map<?, ?> map, final String key, final int defaultValue) {
        final Object value = map.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    public static int readInt(final Map<?, ?> map, final String sectionKey, final String key, final int defaultValue) {
        final Object value = map.get(sectionKey);
        if (value instanceof Map<?, ?> section) {
            return readInt(section, key, defaultValue);
        }
        return defaultValue;
    }

    public static double readDouble(final Map<?, ?> map, final String sectionKey, final String key, final double defaultValue) {
        final Object value = map.get(sectionKey);
        if (value instanceof Map<?, ?> section) {
            final Object number = section.get(key);
            if (number instanceof Number numericValue) {
                return numericValue.doubleValue();
            }
        }
        return defaultValue;
    }

    public static String readString(final Map<?, ?> map, final String key, final String defaultValue) {
        final Object value = map.get(key);
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return stringValue;
        }
        return defaultValue;
    }
}
