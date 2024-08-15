package pl.kwadratowamasakra.lightspigot.connection.packets.out.status;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPong extends PacketOut {

    private long a;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeLong(a);
    }
}
