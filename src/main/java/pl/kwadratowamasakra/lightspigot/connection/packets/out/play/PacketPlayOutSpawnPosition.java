package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.world.BlockPosition;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutSpawnPosition extends PacketOut {

    private int x;
    private int y;
    private int z;

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeLong(new BlockPosition(x, y, z).toLong());
    }
}
