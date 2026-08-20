package pl.kwadratowamasakra.lightspigot.connection.packets.in.configuration;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketConfigurationInIgnored extends PacketIn {
    @Override
    public void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.skipBytes(packetBuffer.readableBytes());
    }

    @Override
    public void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.CONFIGURATION);
    }

    @Override
    public int getLimit() {
        return 20;
    }
}
