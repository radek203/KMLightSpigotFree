package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketPlayerAbilitiesOut extends PacketOut {

    private int flags;
    private float flyingSpeed;
    private float fieldOfView;

    public PacketPlayerAbilitiesOut(final int flags, final float flyingSpeed, final float fieldOfView) {
        this.flags = flags;
        this.flyingSpeed = flyingSpeed;
        this.fieldOfView = fieldOfView;
    }

    public PacketPlayerAbilitiesOut() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeByte(flags);
        packetBuffer.writeFloat(flyingSpeed);
        packetBuffer.writeFloat(fieldOfView);
    }
}
