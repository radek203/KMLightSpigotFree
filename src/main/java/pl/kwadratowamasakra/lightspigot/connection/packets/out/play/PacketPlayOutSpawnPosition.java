package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutSpawnPosition extends PacketOut {

    private int x;
    private int y;
    private int z;

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeLong(packPosition(x, y, z));
    }

    private long packPosition(final int x, final int y, final int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) z & 0x3FFFFFFL) << 12 | (long) y & 0xFFFL;
    }
}
