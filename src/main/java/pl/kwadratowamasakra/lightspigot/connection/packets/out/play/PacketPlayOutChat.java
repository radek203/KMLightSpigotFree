package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutChat extends PacketOut {

    private String message;
    private byte type;

    static void writeTextComponentNbt(final PacketBuffer buffer, final String text) {
        final byte[] bytes = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        buffer.writeByte(8); // anonymous NBT string
        buffer.writeShort(bytes.length);
        buffer.writeBytes(bytes);
    }

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isEqualOrHigher(Version.V1_19)) {
            if (connection.getVersion().isEqualOrHigher(Version.V1_20_3)) {
                writeTextComponentNbt(packetBuffer, message);
            } else {
                packetBuffer.writeString(String.format("{\"text\": \"%s\"}", message));
            }
            packetBuffer.writeBoolean(type == 2); // overlay/action bar
            return;
        }
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", message));
        packetBuffer.writeByte(type);
        if (connection.getVersion().isEqualOrHigher(Version.V1_16)) {
            packetBuffer.writeLong(0L);
            packetBuffer.writeLong(0L);
        }
    }
}
