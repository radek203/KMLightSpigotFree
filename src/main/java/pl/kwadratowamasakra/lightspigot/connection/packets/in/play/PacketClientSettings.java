package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketClientSettings extends PacketIn {

    private String lang;
    private int view;
    private int chatVisibility;
    private boolean enableColors;
    private int modelPartFlags;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        lang = packetBuffer.readString(packetBuffer.readVarInt());
        view = packetBuffer.readByte();
        chatVisibility = packetBuffer.readByte();
        enableColors = packetBuffer.readBoolean();
        modelPartFlags = packetBuffer.readUnsignedByte();
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
