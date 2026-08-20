package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutUpdateViewPosition extends PacketOut {

    private int chunkX;
    private int chunkZ;

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(chunkX);
        packetBuffer.writeVarInt(chunkZ);
    }
}
