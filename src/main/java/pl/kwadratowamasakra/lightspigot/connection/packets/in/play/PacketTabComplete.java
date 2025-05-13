package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketTabComplete extends PacketIn {

    private String message;
    private long targetBlock;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        message = packetBuffer.readString(Math.min(packetBuffer.readVarInt(), 32767));
        final boolean flag = packetBuffer.readBoolean();

        if (flag) {
            targetBlock = packetBuffer.readLong();
        }
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
