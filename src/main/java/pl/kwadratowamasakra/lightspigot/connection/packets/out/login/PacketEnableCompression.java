package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketEnableCompression extends PacketOut {

    private int compressionThreshold;

    public PacketEnableCompression(final int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    public PacketEnableCompression() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(compressionThreshold);
    }
}
