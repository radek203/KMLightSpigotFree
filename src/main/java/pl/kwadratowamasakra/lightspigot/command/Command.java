package pl.kwadratowamasakra.lightspigot.command;

import java.util.List;

/**
 * The Command class represents a command that can be executed.
 * It includes the command name, aliases, and the handler that will execute the command.
 */
public abstract class Command {

    private final String commandName;
    private final List<String> aliases;

    /**
     * Constructs a new Command with the specified command name
     *
     * @param commandName The name of the command.
     */
    public Command(final String commandName) {
        this.commandName = commandName;
        aliases = List.of();
    }

    /**
     * Constructs a new Command with the specified command name, aliases.
     *
     * @param commandName The name of the command.
     * @param aliases     The aliases of the command.
     */
    public Command(final String commandName, final List<String> aliases) {
        this.commandName = commandName;
        this.aliases = aliases;
    }

    /**
     * Gets the name of the command.
     * This can be used to identify the command.
     *
     * @return the name of the command.
     */
    public final String getCommandName() {
        return commandName;
    }

    /**
     * Gets the aliases of the command.
     * These can be used as alternative names for the command.
     *
     * @return a list of aliases for the command.
     */
    public final List<String> getAliases() {
        return aliases;
    }

    /**
     * Handles the execution of the command.
     * This method is called when the command is executed.
     *
     * @param sender The sender of the command.
     * @param args   The arguments provided with the command.
     */
    public abstract void handle(final CommandSender sender, final String[] args);
}
