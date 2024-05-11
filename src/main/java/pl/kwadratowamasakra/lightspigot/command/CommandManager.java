package pl.kwadratowamasakra.lightspigot.command;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.PlayerCommandEvent;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The CommandManager class manages the commands in the server.
 * It provides methods to add a command, retrieve a command by its name, and handle the execution of a command.
 */
public class CommandManager {

    private static final Pattern COMPILE = Pattern.compile("/");
    private final List<Command> commands = new ArrayList<>();

    /**
     * Adds a command to the command manager.
     * The command is only added if there is no existing command with the same name.
     *
     * @param command The command to be added.
     */
    public final void addCommand(final Command command) {
        final Command check = getCommandByName(command.getCommandName());
        if (check == null) {
            commands.add(command);
        }
    }

    /**
     * Retrieves a command by its name.
     *
     * @param commandName The name of the command.
     * @return The command if found, null otherwise.
     */
    private Command getCommandByName(final String commandName) {
        for (final Command command : commands) {
            if (command.getCommandName().equals(commandName) || command.getAliases().contains(commandName)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Handles the execution of a command.
     * If the command does not exist, a message will be sent to the sender.
     *
     * @param server  The server instance.
     * @param message The command message (e.g. /command arg1 arg2) without the slash.
     * @param sender  The sender of the command.
     */
    public final void handleCommand(final LightSpigotServer server, final String message, final CommandSender sender) {
        final String[] args = COMPILE.matcher(message.toLowerCase()).replaceFirst("").split(" ");
        final Command command = getCommandByName(args[0]);
        server.getLogger().info("CommandSender: " + sender.getNameString() + " issued command: " + message);
        if (command == null) {
            sender.sendMessage(ChatUtil.fixColor(server.getConfig().getCommandNotExists()));
            return;
        }

        final PlayerCommandEvent e = new PlayerCommandEvent(sender, true, null, command);
        if (sender instanceof PlayerConnection) {
            server.getEventManager().handleEvent(e);
        }

        if (!e.isAccepted()) {
            sender.sendMessage(e.getMessage());
            return;
        }

        final int length = args.length;
        final String[] finalArgs = new String[length - 1];
        System.arraycopy(args, 1, finalArgs, 0, length - 1);
        e.getCommand().handle(sender, finalArgs);
    }
}
