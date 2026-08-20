package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PacketLoginOutSuccess extends PacketOut {

    private UUID uuid;
    private String username;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isEqualOrHigher(Version.V1_16)) {
            packetBuffer.writeUUID(uuid);
        } else {
            packetBuffer.writeString(uuid.toString());
        }
        packetBuffer.writeString(username);
        if (connection.getVersion().isEqualOrHigher(Version.V1_19)) {
            packetBuffer.writeVarInt(0); // profile properties
        }
        if (connection.getVersion().isInRange(Version.V1_20_5, Version.V1_21)) {
            packetBuffer.writeBoolean(false); // strict error handling
        }
        if (connection.getVersion().isEqualOrHigher(Version.V26_2)) {
            packetBuffer.writeUUID(uuid); // session id
        }
    }

}
