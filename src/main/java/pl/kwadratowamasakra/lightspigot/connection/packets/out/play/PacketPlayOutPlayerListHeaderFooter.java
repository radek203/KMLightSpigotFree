package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutPlayerListHeaderFooter extends PacketOut {

    private String header;
    private String footer;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isEqualOrHigher(Version.V1_20_3)) {
            PacketPlayOutChat.writeTextComponentNbt(packetBuffer, header);
            PacketPlayOutChat.writeTextComponentNbt(packetBuffer, footer);
            return;
        }
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", header));
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", footer));
    }
}
