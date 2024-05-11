package pl.kwadratowamasakra.lightspigot.connection.packets.out.status;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

public class PacketServerInfo extends PacketOut {

    private static final String TEMPLATE = "{ \"version\": { \"name\": \"%s\", \"protocol\": %d }, \"players\": { \"max\": %d, \"online\": %d, \"sample\": [] }, \"description\": %s }";

    private String version;
    private int protocol;
    private int maxPlayers;
    private int onlinePlayers;
    private String description;

    public PacketServerInfo(final String version, final int protocol, final int maxPlayers, final int onlinePlayers, final String description) {
        this.version = version;
        this.protocol = protocol;
        this.maxPlayers = maxPlayers;
        this.onlinePlayers = onlinePlayers;
        this.description = description;
    }

    public PacketServerInfo() {

    }

    private static String getResponseJson(final String version, final int protocol, final int maxPlayers, final int online, final String description) {
        return String.format(TEMPLATE, version, protocol, maxPlayers, online, description);
    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(getResponseJson(version, protocol, maxPlayers, onlinePlayers, ChatUtil.fixColor("{\"text\": \"" + description + "\"}")));
    }

}
