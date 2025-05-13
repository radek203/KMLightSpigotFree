package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketEntityAction extends PacketIn {

    private int entityID;
    private PacketEntityAction.Action action;
    private int auxData;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        entityID = packetBuffer.readVarInt();
        action = packetBuffer.readEnumValue(Action.class);
        auxData = packetBuffer.readVarInt();
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 15;
    }


    public enum Action {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        START_RIDING_JUMP, //RIDING_JUMP - 1.8
        STOP_RIDING_JUMP, //OPEN_INVENTORY - 1.8
        OPEN_INVENTORY,
        START_FALL_FLYING
    }

}
