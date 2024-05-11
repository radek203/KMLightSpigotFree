package pl.kwadratowamasakra.lightspigot.command;

/**
 * The CommandSender interface provides a contract for command sender classes.
 * It provides methods to get the name of the sender, check if the sender has a specific permission, and send a message to the sender.
 * Console and player classes can implement this interface to send commands and messages.
 * In future, it can be used to send commands from other sources, such as a web interface.
 */
public interface CommandSender {

    /**
     * Get name of the command sender. (e.g. player name or console)
     */
    String getNameString();

    /**
     * Checks if the command sender has a specific permission.
     * This can be used to control access to certain commands or features.
     *
     * @param permission The permission to check for.
     * @return true if the sender has the permission, false otherwise.
     */
    boolean hasPermission(final String permission);

    /**
     * Sends a message to the command sender.
     * This can be used to provide feedback or information to the sender.
     *
     * @param message The message to send.
     */
    void sendMessage(final String message);

}
