package pl.kwadratowamasakra.lightspigot.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * The UUIDUtil class provides utility methods for handling UUIDs.
 * It includes methods to get an offline mode UUID from a username, and to convert a string to a UUID.
 */
public class UUIDUtil {

    /**
     * A pattern that matches a UUID string without hyphens.
     * The pattern is used to insert hyphens into the string at the correct positions.
     */
    private static final Pattern COMPILE = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");

    /**
     * Returns the offline mode UUID for the specified username.
     * The UUID is generated from the string "OfflinePlayer:" followed by the username, encoded in UTF-8.
     *
     * @param username The username to get the offline mode UUID for.
     * @return The offline mode UUID for the username, or null if the username is null or blank.
     */
    public static UUID getOfflineModeUUID(final String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Converts a string to a UUID.
     * If the string contains hyphens, it is converted directly to a UUID.
     * If the string does not contain hyphens, they are inserted at the correct positions using the COMPILE pattern.
     *
     * @param str The string to convert to a UUID.
     * @return The UUID, or null if the string is null or blank.
     */
    public static UUID fromString(final String str) {
        if (str == null || str.isBlank()) {
            return null;
        }
        if (str.contains("-")) {
            return UUID.fromString(str);
        }
        return UUID.fromString(COMPILE.matcher(str).replaceFirst("$1-$2-$3-$4-$5"));
    }

}
