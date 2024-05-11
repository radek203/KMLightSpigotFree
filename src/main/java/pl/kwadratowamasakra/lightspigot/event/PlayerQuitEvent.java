package pl.kwadratowamasakra.lightspigot.event;

import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PlayerQuitEvent extends Event {

    private final PlayerConnection connection;

    public PlayerQuitEvent(final PlayerConnection connection) {
        this.connection = connection;
    }

    public final PlayerConnection getConnection() {
        return connection;
    }
}
