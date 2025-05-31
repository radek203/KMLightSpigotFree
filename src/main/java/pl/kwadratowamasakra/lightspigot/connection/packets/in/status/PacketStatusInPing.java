package pl.kwadratowamasakra.lightspigot.connection.packets.in.status;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.status.PacketStatusOutPong;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketStatusInPing extends PacketIn {

    private long a;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        a = packetBuffer.readLong();
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.STATUS);
        connection.sendPacketAndClose(new PacketStatusOutPong(a));
    }

    @Override
    public final int getLimit() {
        return 3;
    }
}
