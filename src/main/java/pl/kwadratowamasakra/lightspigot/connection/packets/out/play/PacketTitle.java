package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketTitle extends PacketOut {

    private String message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;
    private PacketTitle.Type type;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
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
