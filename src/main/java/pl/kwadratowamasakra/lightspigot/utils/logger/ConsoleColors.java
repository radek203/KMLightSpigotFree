package pl.kwadratowamasakra.lightspigot.utils.logger;

/**
 * The ConsoleColors class provides color codes that can be used to change the color of console output.
 * It includes color codes for different colors, bold text, underlined text, background colors, high intensity colors, bold high intensity colors, and high intensity background colors.
 */
public class ConsoleColors {
    // Reset
    static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    static final String BLACK = "\033[0;30m";   // BLACK
    static final String RED = "\033[0;31m";     // RED
    static final String GREEN = "\033[0;32m";   // GREEN
    static final String YELLOW = "\033[0;33m";  // YELLOW
    static final String BLUE = "\033[0;34m";    // BLUE
    static final String PURPLE = "\033[0;35m";  // PURPLE
    static final String CYAN = "\033[0;36m";    // CYAN
    static final String WHITE = "\033[0;37m";   // WHITE

    // High Intensity
    static final String BLACK_BRIGHT = "\033[0;90m";   // BLACK
    static final String RED_BRIGHT = "\033[0;91m";     // RED
    static final String GREEN_BRIGHT = "\033[0;92m";   // GREEN
    static final String YELLOW_BRIGHT = "\033[0;93m";  // YELLOW
    static final String BLUE_BRIGHT = "\033[0;94m";    // BLUE
    static final String PURPLE_BRIGHT = "\033[0;95m";  // PURPLE
    static final String CYAN_BRIGHT = "\033[0;96m";    // CYAN
    static final String WHITE_BRIGHT = "\033[0;97m";   // WHITE

    // Bold High Intensity
    static final String BLACK_BOLD_BRIGHT = "\033[1;90m";   // BLACK
    static final String RED_BOLD_BRIGHT = "\033[1;91m";     // RED
    static final String GREEN_BOLD_BRIGHT = "\033[1;92m";   // GREEN
    static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";  // YELLOW
    static final String BLUE_BOLD_BRIGHT = "\033[1;94m";    // BLUE
    static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";  // PURPLE
    static final String CYAN_BOLD_BRIGHT = "\033[1;96m";    // CYAN
    static final String WHITE_BOLD_BRIGHT = "\033[1;97m";   // WHITE
}
