package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketPlayerAbilitiesIn extends PacketIn {

    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        final byte b0 = packetBuffer.readByte();
        invulnerable = (b0 & 1) > 0;
        flying = (b0 & 2) > 0;
        allowFlying = (b0 & 4) > 0;
        creativeMode = (b0 & 8) > 0;
        flySpeed = packetBuffer.readFloat();
        walkSpeed = packetBuffer.readFloat();
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 6;
    }
}
