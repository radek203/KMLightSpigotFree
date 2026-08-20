package pl.kwadratowamasakra.lightspigot.connection.packets.in.login;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutFeatureFlags;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutFinish;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutRegistryData;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration.PacketConfigurationOutTags;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolConfiguration;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketLoginInAcknowledged extends PacketIn {
    @Override
    public void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
    }

    @Override
    public void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.LOGIN);
        connection.setConnectionState(ConnectionState.CONFIGURATION);
        for (final byte[] registry : ProtocolConfiguration.registries(connection.getVersion())) {
            connection.writePacket(new PacketConfigurationOutRegistryData(registry));
        }
        connection.writePacket(new PacketConfigurationOutFeatureFlags());
        connection.writePacket(new PacketConfigurationOutTags());
        connection.sendPacket(new PacketConfigurationOutFinish());
    }

    @Override
    public int getLimit() {
        return 1;
    }
}
