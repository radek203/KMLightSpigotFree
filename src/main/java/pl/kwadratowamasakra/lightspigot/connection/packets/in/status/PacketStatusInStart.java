package pl.kwadratowamasakra.lightspigot.connection.packets.in.status;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.status.PacketStatusOutServerInfo;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.PlayerServerStatusEvent;

public class PacketStatusInStart extends PacketIn {

    @Override
    public void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.STATUS);
        if (server.getConfig().isProxyEnabled()) {
            connection.closeConnection();
            return;
        }
        final PlayerServerStatusEvent e = new PlayerServerStatusEvent(server.getConfig().getMotdVersion(), connection.getVersion().getProtocolNumber(), server.getConnectionManager().getMaxPlayers(), server.getConnectionManager().getOnlinePlayers(), server.getConfig().getMotdDescription());
        server.getEventManager().handleEvent(e);
        connection.sendPacket(new PacketStatusOutServerInfo(e.getVersion(), e.getProtocol(), e.getMaxPlayers(), e.getOnlinePlayers(), e.getDescription()));
    }

    @Override
    public final int getLimit() {
        return 3;
    }

}
