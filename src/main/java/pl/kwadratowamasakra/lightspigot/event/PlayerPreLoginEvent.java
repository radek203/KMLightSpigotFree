package pl.kwadratowamasakra.lightspigot.event;

import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PlayerPreLoginEvent extends Event {

    private final PlayerConnection connection;
    private boolean accepted;
    private String reason;

    public PlayerPreLoginEvent(final PlayerConnection connection, final boolean accepted, final String reason) {
        this.connection = connection;
        this.accepted = accepted;
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

    public final String getReason() {
        return reason;
    }

    public final void setReason(final String reason) {
        this.reason = reason;
    }
}
