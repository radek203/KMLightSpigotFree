package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketPlayInAbilities extends PacketIn {

    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        final byte flags = packetBuffer.readByte();
        invulnerable = (flags & 0x01) != 0;
        flying = (flags & 0x02) != 0;
        allowFlying = (flags & 0x04) != 0;
        creativeMode = (flags & 0x08) != 0;
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
