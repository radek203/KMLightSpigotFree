package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutPosition extends PacketOut {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int teleportId;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isEqualOrHigher(Version.V1_21_2)) {
            packetBuffer.writeVarInt(teleportId);
            packetBuffer.writeDouble(x);
            packetBuffer.writeDouble(y);
            packetBuffer.writeDouble(z);
            packetBuffer.writeDouble(0.0D);
            packetBuffer.writeDouble(0.0D);
            packetBuffer.writeDouble(0.0D);
            packetBuffer.writeFloat(yaw);
            packetBuffer.writeFloat(pitch);
            packetBuffer.writeInt(0); // relative position/rotation flags
            return;
        }
        packetBuffer.writeDouble(x);
        packetBuffer.writeDouble(y);
        packetBuffer.writeDouble(z);
        packetBuffer.writeFloat(yaw);
        packetBuffer.writeFloat(pitch);
        packetBuffer.writeByte(0x00);

        if (connection.getVersion().isEqualOrHigher(Version.V1_9)) {
            packetBuffer.writeVarInt(teleportId);
        }
        if (connection.getVersion().isInRange(Version.V1_17, Version.V1_19_3)) {
            packetBuffer.writeBoolean(false); // do not dismount
        }
    }

}
