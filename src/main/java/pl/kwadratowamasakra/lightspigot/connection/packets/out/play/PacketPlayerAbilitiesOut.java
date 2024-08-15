package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayerAbilitiesOut extends PacketOut {

    private int flags;
    private float flyingSpeed;
    private float fieldOfView;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeByte(flags);
        packetBuffer.writeFloat(flyingSpeed);
        packetBuffer.writeFloat(fieldOfView);
    }
}
