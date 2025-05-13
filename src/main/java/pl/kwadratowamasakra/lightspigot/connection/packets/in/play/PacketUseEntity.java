package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketUseEntity extends PacketIn {

    private int entityId;
    private PacketUseEntity.Action action;
    private float hitVecX;
    private float hitVecY;
    private float hitVecZ;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        entityId = packetBuffer.readVarInt();
        action = packetBuffer.readEnumValue(Action.class);

        if (action == PacketUseEntity.Action.INTERACT_AT) {
            hitVecX = packetBuffer.readFloat();
            hitVecY = packetBuffer.readFloat();
            hitVecZ = packetBuffer.readFloat();
        }
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 30;
    }

    public enum Action {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }
}
