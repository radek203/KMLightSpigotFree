package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayerPosLook extends PacketOut {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeDouble(x);
        packetBuffer.writeDouble(y);
        packetBuffer.writeDouble(z);
        packetBuffer.writeFloat(yaw);
        packetBuffer.writeFloat(pitch);
        packetBuffer.writeByte(0x08);
    }

}
