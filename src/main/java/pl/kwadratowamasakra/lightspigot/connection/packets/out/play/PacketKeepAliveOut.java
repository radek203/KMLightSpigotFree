package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketKeepAliveOut extends PacketOut {

    private int id;

    public PacketKeepAliveOut(final int id) {
        this.id = id;
    }

    public PacketKeepAliveOut() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(id);
    }
}
