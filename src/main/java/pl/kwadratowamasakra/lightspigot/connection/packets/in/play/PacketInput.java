package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketInput extends PacketIn {

    private float strafeSpeed;
    private float forwardSpeed;
    private boolean jumping;
    private boolean sneaking;

    @Override
    public final void read(final PacketBuffer packetBuffer) {
        strafeSpeed = packetBuffer.readFloat();
        forwardSpeed = packetBuffer.readFloat();
        final byte b0 = packetBuffer.readByte();
        jumping = (b0 & 1) > 0;
        sneaking = (b0 & 2) > 0;
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 125;
    }
}
