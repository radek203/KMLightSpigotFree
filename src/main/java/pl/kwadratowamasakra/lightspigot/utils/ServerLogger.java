package pl.kwadratowamasakra.lightspigot.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * The ServerLogger class provides logging functionality for the server.
 * It includes methods to start the logger thread, log debug messages, connection messages, info messages, and error messages, and save logs to a file.
 */
public class ServerLogger {

    private static final Pattern PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");
    private final List<String> logs = new ArrayList<>();
    private final DateTimeFormatter dateTimeFormatter;
    private final ZoneId zoneId;
    private final boolean debugMode;
    private Thread thread;
    private Timer saveTimer;

    /**
     * Constructs a new ServerLogger with the specified date time formatter, zone ID, and debug mode.
     *
     * @param dateTimeFormatter The date time formatter to use for formatting the date and time in logs.
     * @param zoneId            The zone ID to use for getting the current date and time.
     * @param debugMode         The debug mode. If true, debug messages are logged.
     */
    public ServerLogger(final DateTimeFormatter dateTimeFormatter, final ZoneId zoneId, final boolean debugMode) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.zoneId = zoneId;
        this.debugMode = debugMode;
    }

    /**
     * Starts the logger thread.
     * The logger thread starts a timer that saves logs to a file at fixed intervals.
     */
    public final void startLoggerThread() {
        thread = new Thread(this::startTimer, "ServerLoggerThread");
        thread.start();
    }

    /**
     * Starts a timer that saves logs to a file at fixed intervals.
     * The timer is scheduled to save logs every 21600000 milliseconds (6 hours).
     */
    private void startTimer() {
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                saveToFile();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0L, 21600000L);
        saveTimer = timer;
    }

    /**
     * Returns the current date and time formatted using the date time formatter and zone ID.
     *
     * @return The current date and time formatted as a string.
     */
    private String getCurrentDate() {
        return dateTimeFormatter.format(ZonedDateTime.now(zoneId).toLocalDateTime());
    }

    /**
     * Saves a log message to the list of logs and prints it to the console.
     *
     * @param log The log message to save and print.
     */
    private void saveAndLog(final String log) {
        logs.add(log);
        System.out.println(log);
    }

    public final void debug(final String message) {
        if (debugMode) {
            saveAndLog(getCurrentDate() + ConsoleColors.YELLOW + " [DEBUG] " + ConsoleColors.RESET + message);
        }
    }

    public final void connection(final String message) {
        saveAndLog(getCurrentDate() + ConsoleColors.CYAN_BRIGHT + " [CONNECTION] " + ConsoleColors.RESET + message);
    }

    public final void info(final String message) {
        saveAndLog(getCurrentDate() + " " + message);
    }

    public final void error(final String message, final String error) {
        saveAndLog(getCurrentDate() + ConsoleColors.RED + " [ERROR] " + ConsoleColors.RESET + message + "\n" + error);
    }

    /**
     * Saves the logs to a file.
     * The logs are saved to a file with the current date and time as the file name in the "logs" directory.
     * The logs are then cleared from the list of logs.
     */
    private void saveToFile() {
        if (logs.isEmpty()) {
            return;
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss");
        final String logDate = formatter.format(ZonedDateTime.now(zoneId).toLocalDateTime());

        final File file = new File("logs");
        if (!file.exists()) {
            file.mkdir();
        }

        try (final PrintWriter writer = new PrintWriter("logs/" + logDate + ".txt", StandardCharsets.UTF_8)) {
            for (final String log : logs) {
                final String logWithoutColors = PATTERN.matcher(log).replaceAll("");
                writer.println(logWithoutColors);
            }
        } catch (final IOException ignored) {

        }
        logs.clear();
    }

    /**
     * Stops the logger thread and saves the logs to a file.
     * The timer is cancelled, the logger thread is interrupted, and the logs are saved to a file.
     */
    public final void stop() {
        if (saveTimer != null) {
            saveTimer.cancel();
        }
        if (thread != null) {
            thread.interrupt();
        }
        saveToFile();
    }

}
