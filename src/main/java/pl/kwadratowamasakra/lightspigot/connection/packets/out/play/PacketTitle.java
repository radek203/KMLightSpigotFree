package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketTitle extends PacketOut {

    private PacketTitle.Type type;
    private String message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;

    public PacketTitle(final String message, final int fadeInTime, final int displayTime, final int fadeOutTime, final PacketTitle.Type type) {
        this.message = message;
        this.fadeInTime = fadeInTime;
        this.displayTime = displayTime;
        this.fadeOutTime = fadeOutTime;
        this.type = type;
    }

    public PacketTitle() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeEnumValue(type);

        if (type == PacketTitle.Type.TITLE || type == PacketTitle.Type.SUBTITLE) {
            packetBuffer.writeString(String.format("{\"text\": \"%s\"}", message));
        }

        if (type == PacketTitle.Type.TIMES) {
            packetBuffer.writeInt(fadeInTime);
            packetBuffer.writeInt(displayTime);
            packetBuffer.writeInt(fadeOutTime);
        }
    }

    public enum Type {
        TITLE,
        SUBTITLE,
        TIMES,
        CLEAR,
        RESET
    }

}
