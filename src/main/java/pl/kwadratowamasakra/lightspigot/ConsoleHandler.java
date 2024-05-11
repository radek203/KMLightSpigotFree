package pl.kwadratowamasakra.lightspigot;

import pl.kwadratowamasakra.lightspigot.command.ConsoleCommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * The ConsoleHandler class handles console input for the server.
 * It includes methods to start and stop the console thread, and to read and handle console commands.
 */
class ConsoleHandler {

    private final LightSpigotServer server;
    private final ConsoleCommandSender sender;
    private Thread thread;
    private BufferedReader reader;

    /**
     * Constructs a new ConsoleHandler with the specified server.
     *
     * @param server The server that the console handler is associated with.
     */
    ConsoleHandler(final LightSpigotServer server) {
        this.server = server;
        sender = new ConsoleCommandSender(server);
    }

    /**
     * Starts the console thread.
     * The console thread reads and handles console commands.
     */
    final void startConsoleThread() {
        thread = new Thread(this::runConsoleReader, "ConsoleThread");
        thread.start();
    }

    /**
     * Reads and handles console commands.
     * This method is run in the console thread.
     * It reads console commands and handles them using the server's command manager.
     */
    private void runConsoleReader() {
        reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        while (!server.isClosing()) {

            try {
                final String readLine = reader.readLine();
                server.getCommandManager().handleCommand(server, readLine, sender);
            } catch (final IOException ignored) {

            }
        }

        closeReader();
    }

    /**
     * Stops the console thread and closes the reader.
     * The console thread is interrupted and the reader is closed.
     */
    final void stopThread() {
        thread.interrupt();
        closeReader();
    }

    /**
     * Closes the reader.
     * If an IOException occurs, it is ignored.
     */
    private void closeReader() {
        try {
            reader.close();
        } catch (final IOException ignored) {

        }
    }

}
