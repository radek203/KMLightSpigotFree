package pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.registry.ProtocolConfiguration;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketConfigurationOutTags extends PacketOut {
    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        ProtocolConfiguration.writeRequiredTags(connection.getVersion(), packetBuffer);
    }
}
