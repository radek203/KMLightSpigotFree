package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketResourcePackStatus extends PacketIn {

    private String hash;
    private PacketResourcePackStatus.Action status;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        hash = packetBuffer.readString(Math.min(packetBuffer.readVarInt(), 40));
        status = packetBuffer.readEnumValue(Action.class);
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 3;
    }

    public enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED
    }
}
