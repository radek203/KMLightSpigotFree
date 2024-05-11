package pl.kwadratowamasakra.lightspigot.connection.packets.out.status;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketPong extends PacketOut {

    private long a;

    public PacketPong(final long a) {
        this.a = a;
    }

    public PacketPong() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeLong(a);
    }
}
