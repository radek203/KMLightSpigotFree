package pl.kwadratowamasakra.lightspigot.utils.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLogger {

    private static final Logger CONSOLE_LOGGER = LogManager.getLogger("ConsoleLogger");
    private static final Logger FILE_LOGGER = LogManager.getLogger("FileLogger");
    private final boolean debugMode;

    public ServerLogger(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void debug(String message) {
        if (debugMode) {
            CONSOLE_LOGGER.debug(ConsoleColors.YELLOW + "{}" + ConsoleColors.RESET, message);
            FILE_LOGGER.debug(message);
        }
    }

    public void connection(String title, String message) {
        CONSOLE_LOGGER.info(ConsoleColors.CYAN_BRIGHT + "[CONNECTION] " + ConsoleColors.CYAN + "{} " + ConsoleColors.RESET + "{}", title, message);
        FILE_LOGGER.info("{} {}", title, message);
    }

    public void info(String message) {
        CONSOLE_LOGGER.info(message);
        FILE_LOGGER.info(message);
    }

    public void success(String message) {
        CONSOLE_LOGGER.info(ConsoleColors.GREEN_BRIGHT + "{}" + ConsoleColors.RESET, message);
        FILE_LOGGER.info(message);
    }

    public void error(String message, String error) {
        CONSOLE_LOGGER.error(ConsoleColors.RED + "{}\n{}" + ConsoleColors.RESET, message, error);
        FILE_LOGGER.error("{}\n{}", message, error);
    }
}
