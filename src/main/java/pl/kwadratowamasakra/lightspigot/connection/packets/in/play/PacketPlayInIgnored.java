package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

/**
 * Accepts protocol packets which do not affect this lightweight server's state.
 */
public class PacketPlayInIgnored extends PacketIn {
    @Override
    public void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.skipBytes(packetBuffer.readableBytes());
    }

    @Override
    public void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public int getLimit() {
        return 150;
    }
}
