package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketPlayInCommand extends PacketPlayInChat {
    @Override
    public void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        message = '/' + packetBuffer.readString(Math.min(packetBuffer.readVarInt(), 99));
    }
}
