package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketDisconnect extends PacketOut {

    private String reason;

    public PacketDisconnect(final String reason) {
        this.reason = reason;
    }

    public PacketDisconnect() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", reason));
    }
}
