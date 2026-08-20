package pl.kwadratowamasakra.lightspigot.connection.packets.out.configuration;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketConfigurationOutRegistryData extends PacketOut {

    private byte[] data;

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeBytes(data);
    }
}
