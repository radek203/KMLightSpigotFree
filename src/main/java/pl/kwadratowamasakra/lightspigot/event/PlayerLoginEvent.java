package pl.kwadratowamasakra.lightspigot.event;

import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PlayerLoginEvent extends Event {

    private final PlayerConnection connection;
    private final Location location;

    public PlayerLoginEvent(final PlayerConnection connection, final Location location) {
        this.connection = connection;
        this.location = location;
    }

    public final PlayerConnection getConnection() {
        return connection;
    }

    public final Location getLocation() {
        return location;
    }
}
