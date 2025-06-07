package pl.kwadratowamasakra.lightspigot.utils;

import java.util.regex.Pattern;

/**
 * The ChatUtil class provides utility methods for handling chat-related operations.
 */
public class ChatUtil {

    /**
     * A pattern that matches valid characters in a string.
     */
    private static final Pattern VALID_CHARS_PATTERN = Pattern.compile("[A-Za-z0-9_]");

    /**
     * Translates alternate color codes in a string and returns the result.
     *
     * @param s The string to translate color codes in.
     * @return The string with color codes translated.
     */
    public static String fixColor(final String s) {
        return translateAlternateColorCodes(s);
    }

    /**
     * Translates alternate color codes in a string and returns the result.
     * The color codes are represented by '&' followed by a character.
     *
     * @param textToTranslate The string to translate color codes in.
     * @return The string with color codes translated.
     */
    private static String translateAlternateColorCodes(final String textToTranslate) {
        final char[] chars = textToTranslate.toCharArray();

        final int length = chars.length;
        for (int i = 0; i < length - 1; ++i) {
            if (chars[i] == '&' && "0123456789abcdefklmnorx".indexOf(chars[i + 1]) > -1) {
                chars[i] = 167;
            }
        }

        return new String(chars);
    }

    /**
     * Returns a string that contains the invalid characters in the input string.
     * The invalid characters are those that do not match the VALID_CHARS_PATTERN.
     *
     * @param s The string to check for invalid characters.
     * @return The string of invalid characters.
     */
    public static String getInvalidChars(final String s) {
        return VALID_CHARS_PATTERN.matcher(s).replaceAll("");
    }

}
