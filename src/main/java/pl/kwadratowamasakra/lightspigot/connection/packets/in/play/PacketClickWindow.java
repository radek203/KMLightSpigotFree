package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketClickWindow extends PacketIn {

    private int windowId;
    private int slotId;
    private int usedButton;
    private short actionNumber;
    private short clickedItem;
    private int mode;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        windowId = packetBuffer.readByte();
        slotId = packetBuffer.readShort();
        usedButton = packetBuffer.readByte();
        actionNumber = packetBuffer.readShort();
        mode = packetBuffer.readByte();
        clickedItem = packetBuffer.readShort();
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
    }

    @Override
    public final int getLimit() {
        return 70;
    }

}
