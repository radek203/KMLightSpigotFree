package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketClientStatus extends PacketIn {

    private PacketClientStatus.EnumState status;

    @Override
    public final void read(final PacketBuffer packetBuffer) {
        status = packetBuffer.readEnumValue(EnumState.class);
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 3;
    }

    public enum EnumState {
        PERFORM_RESPAWN,
        REQUEST_STATS,
        OPEN_INVENTORY_ACHIEVEMENT
    }
}
