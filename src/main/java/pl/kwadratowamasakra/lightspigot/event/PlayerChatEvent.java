package pl.kwadratowamasakra.lightspigot.event;

import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PlayerChatEvent extends Event {

    private final PlayerConnection connection;
    private boolean accepted;
    private String message;
    private String reason;

    public PlayerChatEvent(final PlayerConnection connection, final boolean accepted, final String message, final String reason) {
        this.connection = connection;
        this.accepted = accepted;
        this.message = message;
        this.reason = reason;
    }

    public final PlayerConnection getConnection() {
        return connection;
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

    public final String getReason() {
        return reason;
    }

    public final void setReason(final String reason) {
        this.reason = reason;
    }
}
