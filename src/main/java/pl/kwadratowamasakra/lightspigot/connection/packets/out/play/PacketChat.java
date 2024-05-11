package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketChat extends PacketOut {

    private String message;
    private byte type;

    public PacketChat(final String message, final byte type) {
        this.message = message;
        this.type = type;
    }

    public PacketChat() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", message));
        packetBuffer.writeByte(type);
    }
}
