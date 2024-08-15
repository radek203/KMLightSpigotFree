package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

@AllArgsConstructor
@NoArgsConstructor
public class PacketEnableCompression extends PacketOut {

    private int compressionThreshold;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(compressionThreshold);
    }
}
