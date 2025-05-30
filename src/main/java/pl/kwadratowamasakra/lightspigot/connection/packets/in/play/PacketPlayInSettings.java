package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketPlayInSettings extends PacketIn {

    private String lang;
    private int view;
    private int chatVisibility;
    private boolean enableColors;
    private int modelPartFlags;
    private int hand;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        lang = packetBuffer.readString(packetBuffer.readVarInt());
        view = packetBuffer.readByte();
        chatVisibility = packetBuffer.readByte();
        enableColors = packetBuffer.readBoolean();
        modelPartFlags = packetBuffer.readUnsignedByte();
        if (connection.getVersion().isEqualOrHigher(Version.V1_9)) {
            hand = packetBuffer.readVarInt();
        }
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
