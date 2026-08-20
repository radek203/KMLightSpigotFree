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
        if (connection.getVersion().isEqualOrHigher(Version.V1_17)) {
            if (type == Type.CLEAR || type == Type.RESET) {
                packetBuffer.writeBoolean(type == Type.RESET);
            } else if (type == Type.TIMES) {
                packetBuffer.writeInt(fadeInTime);
                packetBuffer.writeInt(displayTime);
                packetBuffer.writeInt(fadeOutTime);
            } else {
                if (connection.getVersion().isEqualOrHigher(Version.V1_20_3)) {
                    PacketPlayOutChat.writeTextComponentNbt(packetBuffer, message);
                } else {
                    packetBuffer.writeString(String.format("{\"text\": \"%s\"}", message));
                }
            }
            return;
        }
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

    @Override
    public int packetIdOverride(final Version version) {
        if (version.isLessThan(Version.V1_17)) {
            return -1;
        }
        final int index = version.getProtocolNumber() - Version.V1_18.getProtocolNumber();
        if (index < 0) {
            return switch (type) {
                case TITLE -> 0x59;
                case SUBTITLE -> 0x57;
                case ACTIONBAR -> 0x41;
                case TIMES -> 0x5A;
                case CLEAR, RESET -> 0x10;
            };
        }
        final int[][] ids = {
                {0x5A, 0x5A, 0x5A, 0x5D, 0x5B, 0x5F, 0x5F, 0x61, 0x63, 0x65, 0x65, 0x6C, 0x6C, 0x6B, 0x6B, 0x6B, 0x70, 0x70, 0x72, 0x72},
                {0x58, 0x58, 0x58, 0x5B, 0x59, 0x5D, 0x5D, 0x5F, 0x61, 0x63, 0x63, 0x6A, 0x6A, 0x69, 0x69, 0x69, 0x6E, 0x6E, 0x70, 0x70},
                {0x41, 0x41, 0x40, 0x43, 0x42, 0x46, 0x46, 0x48, 0x4A, 0x4C, 0x4C, 0x51, 0x51, 0x50, 0x50, 0x50, 0x55, 0x55, 0x57, 0x57},
                {0x5B, 0x5B, 0x5B, 0x5E, 0x5C, 0x60, 0x60, 0x62, 0x64, 0x66, 0x66, 0x6D, 0x6D, 0x6C, 0x6C, 0x6C, 0x71, 0x71, 0x73, 0x73},
                {0x10, 0x10, 0x0D, 0x0D, 0x0C, 0x0E, 0x0E, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0E, 0x0E, 0x0E, 0x0E, 0x0E, 0x0E, 0x0E}
        };
        return ids[type == Type.RESET ? Type.CLEAR.ordinal() : type.ordinal()][index];
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
