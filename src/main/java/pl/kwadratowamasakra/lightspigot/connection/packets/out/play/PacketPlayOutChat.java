package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutChat extends PacketOut {

    private String message;
    private byte type;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", message));
        packetBuffer.writeByte(type);
    }
}
