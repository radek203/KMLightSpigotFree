package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketConfirmTransaction extends PacketIn {

    private int windowId;
    private short uid;
    private boolean accepted;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        windowId = packetBuffer.readByte();
        uid = packetBuffer.readShort();
        accepted = packetBuffer.readByte() != 0;
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 3;
    }
}
