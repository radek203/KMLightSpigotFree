package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PacketLoginSuccess extends PacketOut {

    private UUID uuid;
    private String username;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        packetBuffer.writeString(uuid.toString());
        packetBuffer.writeString(username);
    }

}
