package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketPlayerPosLook extends PacketOut {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PacketPlayerPosLook(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PacketPlayerPosLook() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeDouble(x);
        packetBuffer.writeDouble(y);
        packetBuffer.writeDouble(z);
        packetBuffer.writeFloat(yaw);
        packetBuffer.writeFloat(pitch);
        packetBuffer.writeByte(0x08);
    }

}
