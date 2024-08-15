package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketKickDisconnect extends PacketOut {

    private String reason;

    public PacketKickDisconnect(final String reason) {
        this.reason = reason;
    }

    public PacketKickDisconnect() {

    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", reason));
    }

}
