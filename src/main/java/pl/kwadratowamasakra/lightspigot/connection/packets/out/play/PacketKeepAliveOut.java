package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

@AllArgsConstructor
@NoArgsConstructor
public class PacketKeepAliveOut extends PacketOut {

    private int id;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(id);
    }
}
