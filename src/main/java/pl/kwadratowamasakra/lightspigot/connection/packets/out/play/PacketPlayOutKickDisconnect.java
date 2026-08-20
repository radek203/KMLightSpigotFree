package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutKickDisconnect extends PacketOut {

    private String reason;

    @Override
    public void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isEqualOrHigher(Version.V1_20_3)) {
            PacketPlayOutChat.writeTextComponentNbt(packetBuffer, reason);
            return;
        }
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", reason));
    }

}
