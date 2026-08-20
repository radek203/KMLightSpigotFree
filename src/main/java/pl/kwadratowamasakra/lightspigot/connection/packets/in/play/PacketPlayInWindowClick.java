package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;

public class PacketPlayInWindowClick extends PacketIn {

    private int windowId;
    private int slotId;
    private int usedButton;
    private short actionNumber;
    private ItemStack item;
    private int shift;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        if (connection.getVersion().isEqualOrHigher(Version.V1_20_5)) {
            packetBuffer.skipBytes(packetBuffer.readableBytes());
            return;
        }
        windowId = packetBuffer.readByte();
        if (connection.getVersion().isEqualOrHigher(Version.V1_17_1)) {
            packetBuffer.readVarInt(); // state id
        }
        slotId = packetBuffer.readShort();
        usedButton = packetBuffer.readByte();
        if (connection.getVersion().isEqualOrHigher(Version.V1_17)) {
            shift = connection.getVersion().isEqualOrHigher(Version.V1_17_1)
                    ? packetBuffer.readVarInt() : packetBuffer.readByte();
            final int changedSlots = packetBuffer.readVarInt();
            if (changedSlots < 0 || changedSlots > 128) {
                throw new IllegalArgumentException("Invalid changed slot count: " + changedSlots);
            }
            for (int i = 0; i < changedSlots; i++) {
                packetBuffer.readShort();
                packetBuffer.readItemStack(connection);
            }
            item = packetBuffer.readItemStack(connection);
            return;
        }
        actionNumber = packetBuffer.readShort();
        if (connection.getVersion().isEqualOrHigher(Version.V1_9)) {
            shift = packetBuffer.readVarInt();
        } else {
            shift = packetBuffer.readByte();
        }
        item = packetBuffer.readItemStack(connection);
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
