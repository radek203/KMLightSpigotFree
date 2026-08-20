package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
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
        final Version version = connection == null ? Version.V1_8 : connection.getVersion();
        if (version.isEqualOrHigher(Version.V1_21_11)) {
            packetBuffer.writeString("minecraft:overworld");
        }
        packetBuffer.writeLong(new BlockPosition(x, y, z).toLong(version));
        if (version.isEqualOrHigher(Version.V1_17)) {
            packetBuffer.writeFloat(0.0F);
        }
        if (version.isEqualOrHigher(Version.V1_21_11)) {
            packetBuffer.writeFloat(0.0F);
        }
    }
}
