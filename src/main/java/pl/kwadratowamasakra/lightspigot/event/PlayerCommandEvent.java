package pl.kwadratowamasakra.lightspigot.event;

import pl.kwadratowamasakra.lightspigot.command.Command;
import pl.kwadratowamasakra.lightspigot.command.CommandSender;

public class PlayerCommandEvent extends Event {

    private final CommandSender sender;
    private boolean accepted;
    private String message;
    private Command command;

    public PlayerCommandEvent(final CommandSender sender, final boolean accepted, final String message, final Command command) {
        this.sender = sender;
        this.accepted = accepted;
        this.message = message;
        this.command = command;
    }

    public final CommandSender getSender() {
        return sender;
    }

    public final Command getCommand() {
        return command;
    }

    public final void setCommand(final Command command) {
        this.command = command;
    }

    public final boolean isAccepted() {
        return accepted;
    }

    public final void setAccepted(final boolean accepted) {
        this.accepted = accepted;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(final String message) {
        this.message = message;
    }
}
