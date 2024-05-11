package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketCustomPayload extends PacketIn {

    private String channel;
    private PacketBuffer data;

    @Override
    public final void read(final PacketBuffer packetBuffer) {
        channel = packetBuffer.readString(packetBuffer.readVarInt());
        data = new PacketBuffer(packetBuffer.readBytes(packetBuffer.readableBytes()));
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 15;
    }
}
