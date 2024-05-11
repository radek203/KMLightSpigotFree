package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

import java.util.UUID;

public class PacketLoginSuccess extends PacketOut {

    private UUID uuid;
    private String username;

    public PacketLoginSuccess(final UUID uuid, final String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public PacketLoginSuccess() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(uuid.toString());
        packetBuffer.writeString(username);
    }

}
