package pl.kwadratowamasakra.lightspigot.event;

public class PlayerServerStatusEvent extends Event {

    private String version;
    private int protocol;
    private int maxPlayers;
    private int onlinePlayers;
    private String description;

    public PlayerServerStatusEvent(final String version, final int protocol, final int maxPlayers, final int onlinePlayers, final String description) {
        this.version = version;
        this.protocol = protocol;
        this.maxPlayers = maxPlayers;
        this.onlinePlayers = onlinePlayers;
        this.description = description;
    }

    public final String getVersion() {
        return version;
    }

    public final void setVersion(final String version) {
        this.version = version;
    }

    public final int getProtocol() {
        return protocol;
    }

    public final void setProtocol(final int protocol) {
        this.protocol = protocol;
    }

    public final int getMaxPlayers() {
        return maxPlayers;
    }

    public final void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public final int getOnlinePlayers() {
        return onlinePlayers;
    }

    public final void setOnlinePlayers(final int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(final String description) {
        this.description = description;
    }
}
