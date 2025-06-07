package pl.kwadratowamasakra.lightspigot.connection.packets.in;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.PlayerHandshakeEvent;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

public class PacketHandshakingInSetProtocol extends PacketIn {

    private int version;
    private String host;
    private int port;
    private int nextState;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        version = packetBuffer.readVarInt();
        host = packetBuffer.readString();
        port = packetBuffer.readUnsignedShort();
        nextState = packetBuffer.readVarInt();
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.HANDSHAKING);
        if (nextState != 1 && nextState != 2) {
            throw new IllegalArgumentException("Illegal nextState in PacketHandshake");
        }
        connection.setVersion(Version.of(version));

        if (nextState == 2) {
            final boolean versionSupported = isPlayerVersionSupported(connection, server);
            final PlayerHandshakeEvent e = new PlayerHandshakeEvent(connection, versionSupported, versionSupported ? null : ChatUtil.fixColor(server.getConfig().getUnsupportedVersionMessage()));
            server.getEventManager().handleEvent(e);
            if (!e.isAccepted()) {
                connection.disconnect(e.getReason());
                return;
            }
        } else {
            if (server.getConfig().isProxyEnabled()) {
                connection.closeConnection();
                return;
            }
        }
        server.getLogger().connection("PacketHandshaking", "Client: " + connection.getIp() + " Version: " + connection.getVersion() + " State: " + nextState);
        connection.setConnectionState(nextState == 1 ? ConnectionState.STATUS : ConnectionState.LOGIN);
    }

    @Override
    public final int getLimit() {
        return 3;
    }

    private boolean isPlayerVersionSupported(final PlayerConnection connection, final LightSpigotServer server) {
        return connection.getVersion().getProtocolNumber() >= server.getConfig().getMinVersion() && connection.getVersion().getProtocolNumber() <= server.getConfig().getMaxVersion();
    }

}
