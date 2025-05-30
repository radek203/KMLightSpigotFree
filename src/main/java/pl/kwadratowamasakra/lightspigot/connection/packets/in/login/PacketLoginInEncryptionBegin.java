package pl.kwadratowamasakra.lightspigot.connection.packets.in.login;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketLoginInEncryptionBegin extends PacketIn {

    private static final byte[] BYTES = new byte[0];
    private byte[] secretKeyEncrypted = BYTES;
    private byte[] verifyTokenEncrypted = BYTES;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        secretKeyEncrypted = packetBuffer.readBytesArray();
        verifyTokenEncrypted = packetBuffer.readBytesArray();
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.LOGIN);
    }

    @Override
    public final int getLimit() {
        return 0;
    }
}
