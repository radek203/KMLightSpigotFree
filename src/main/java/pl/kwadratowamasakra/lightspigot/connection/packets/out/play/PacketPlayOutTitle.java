package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutTitle extends PacketOut {

    private String message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;
    private PacketPlayOutTitle.Type type;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isLessThan(Version.V1_12_2) && type.ordinal() >= 2) {
            packetBuffer.writeEnumValue(Type.values()[type.ordinal() - 1]);
        } else {
            packetBuffer.writeEnumValue(type);
        }

        if (type == PacketPlayOutTitle.Type.TITLE || type == PacketPlayOutTitle.Type.SUBTITLE) {
            packetBuffer.writeString(String.format("{\"text\": \"%s\"}", message));
        }

        if (type == PacketPlayOutTitle.Type.TIMES) {
            packetBuffer.writeInt(fadeInTime);
            packetBuffer.writeInt(displayTime);
            packetBuffer.writeInt(fadeOutTime);
        }
    }

    public enum Type {
        TITLE,
        SUBTITLE,
        ACTIONBAR, //1.12
        TIMES,
        CLEAR,
        RESET
    }

}
