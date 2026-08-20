package pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketConfigurationOutFeatureFlags extends PacketOut {
    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(1);
        packetBuffer.writeString("minecraft:vanilla");
    }
}
